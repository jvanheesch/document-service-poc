package com.github.jvanheesch.document;

import reactor.core.publisher.Mono;

public interface DocumentRepository {
    Document save(String name, byte[] content);

    byte[] download(Long documentId);
}
