package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

@AllArgsConstructor
@RequestMapping(path = "/documents")
@RestController
public class DocumentController {
    private final DocumentRepository documentRepository;
    private final QueryGateway queryGateway;

    @PostMapping
    public Document upload(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return documentRepository.save(file.getName(), file.getBytes());
    }

    @GetMapping(path = "/{documentId}/status", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DocumentStatus> getDocumentStatus(@PathVariable Long documentId) {
        SubscriptionQueryResult<DocumentStatus, DocumentStatus> statusQueryResult = queryGateway.subscriptionQuery(
                new DocumentStatusQuery(documentId),
                DocumentStatus.class,
                DocumentStatus.class
        );

        return Flux.concat(statusQueryResult.initialResult().flux(), statusQueryResult.updates())
                .onErrorReturn(DocumentStatus.FAILURE)
                .takeUntil(status -> status != DocumentStatus.IN_PROGRESS);
    }
}
