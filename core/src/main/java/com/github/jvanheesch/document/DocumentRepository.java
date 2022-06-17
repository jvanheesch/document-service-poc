package com.github.jvanheesch.document;

public interface DocumentRepository {
    Document save(String name, byte[] content);
}
