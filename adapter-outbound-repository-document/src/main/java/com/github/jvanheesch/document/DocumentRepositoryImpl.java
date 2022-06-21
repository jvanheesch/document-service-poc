package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class DocumentRepositoryImpl implements DocumentRepository, DocumentGenerator {
    private final DocumentJpaRepository documentJpaRepository;
    private final JmsTemplate jmsTemplate;
    private final QueryGateway queryGateway;

    @Override
    public Document save(String name, byte[] content) {
        UUID correlation = UUID.randomUUID();

        var documentDTO = documentJpaRepository.save(DocumentDTO
                .builder()
                .name(name)
                .status(DocumentStatus.IN_PROGRESS)
                .correlation(correlation.toString())
                .build());

        jmsTemplate.convertAndSend("document.request.topic", correlation);

        SubscriptionQueryResult<DocumentStatus, DocumentStatus> statusQueryResult = queryGateway.subscriptionQuery(
                new DocumentStatusQuery(documentDTO.getId()),
                DocumentStatus.class,
                DocumentStatus.class
        );

        return Flux.concat(statusQueryResult.initialResult().flux(), statusQueryResult.updates())
                .filter(status -> status != DocumentStatus.IN_PROGRESS)
                .next()
                .flatMap(status -> switch (status) {
                    case SUCCESS -> Mono.justOrEmpty(documentJpaRepository.findById(documentDTO.getId()));
                    case IN_PROGRESS, FAILURE -> Mono.error(new RuntimeException("failed to save document with id " + documentDTO.getId()));
                })
                .map(this::toDocument)
                .doOnCancel(() -> log.warn("Canceled request to save document with id {}.", documentDTO.getId()))
                .doOnError(e -> log.warn("Error during request to save document with id {}.", documentDTO.getId(), e))
                .block()
                ;
    }

    @QueryHandler
    public DocumentStatus handle(DocumentStatusQuery query) {
        return documentJpaRepository.findById(query.getDocumentId())
                .map(DocumentDTO::getStatus)
                .orElse(null);
    }

    @Override
    public byte[] download(Long documentId) {
        return documentJpaRepository.findById(documentId)
                .map(DocumentDTO::getDocumentserviceUuid)
                .map(documentServiceUuid -> ("content: " + documentServiceUuid).getBytes(StandardCharsets.UTF_8))
                .orElseThrow();
    }

    @Override
    public Mono<byte[]> generateDocument() {
        UUID correlation = UUID.randomUUID();

        var documentDTO = documentJpaRepository.save(DocumentDTO
                .builder()
                .status(DocumentStatus.IN_PROGRESS)
                .correlation(correlation.toString())
                .build());

        jmsTemplate.convertAndSend("document.request.topic", correlation);

        SubscriptionQueryResult<DocumentStatus, DocumentStatus> statusQueryResult = queryGateway.subscriptionQuery(
                new DocumentStatusQuery(documentDTO.getId()),
                DocumentStatus.class,
                DocumentStatus.class
        );

        return Flux.concat(statusQueryResult.initialResult().flux(), statusQueryResult.updates())
                .filter(status -> status != DocumentStatus.IN_PROGRESS)
                .next()
                .flatMap(status -> switch (status) {
                    case SUCCESS -> Mono.justOrEmpty(documentJpaRepository.findById(documentDTO.getId()));
                    case IN_PROGRESS, FAILURE -> Mono.error(new RuntimeException("failed to save document with id " + documentDTO.getId()));
                })
                .map(DocumentDTO::getId)
                .map(this::download)
                .doOnCancel(() -> log.warn("Canceled request to generate document with id {}.", documentDTO.getId()))
                .doOnError(e -> log.warn("Error during request to generate document with id {}.", documentDTO.getId(), e))
                ;
    }

    private Document toDocument(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return null;
        }

        return Document.builder()
                .id(documentDTO.getId())
                .name(documentDTO.getName())
                .build();
    }
}
