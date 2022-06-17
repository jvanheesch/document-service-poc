package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
public class DocumentRepositoryImpl implements DocumentRepository {
    private final DocumentJpaRepository documentJpaRepository;
    private final JmsTemplate jmsTemplate;

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

        return toDocument(documentDTO);
    }

    private Document toDocument(DocumentDTO documentDTO) {
        if (documentDTO == null) {
            return null;
        }

        return Document.builder()
                .id(documentDTO.getId())
                .name(documentDTO.getName())
                .status(documentDTO.getStatus())
                .build();
    }
}
