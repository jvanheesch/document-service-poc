package com.github.jvanheesch.document;

public class DocumentStatusQuery {
    private final long documentId;

    public DocumentStatusQuery(long documentId) {
        this.documentId = documentId;
    }

    public long getDocumentId() {
        return documentId;
    }
}
