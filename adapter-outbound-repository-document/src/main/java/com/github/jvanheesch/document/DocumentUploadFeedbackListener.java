package com.github.jvanheesch.document;

import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
class DocumentUploadFeedbackListener {
    private final DocumentJpaRepository documentJpaRepository;
    private final QueryUpdateEmitter queryUpdateEmitter;

    @JmsListener(destination = "document.feedback.topic", subscription = "myappDocumentQueue")
    void handle(String correlation) {
        var documentDTO = documentJpaRepository.findByCorrelation(correlation).orElseThrow();
        documentDTO.setStatus(DocumentStatus.SUCCESS);
        // pretend documentservice uuid is equal to correlation
        documentDTO.setDocumentserviceUuid(correlation);
        documentJpaRepository.save(documentDTO);
        queryUpdateEmitter.emit(DocumentStatusQuery.class, query -> query.getDocumentId() == documentDTO.getId(), DocumentStatus.SUCCESS);
    }
}
