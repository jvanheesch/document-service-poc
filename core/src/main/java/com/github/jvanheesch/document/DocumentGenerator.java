package com.github.jvanheesch.document;

import reactor.core.publisher.Mono;

public interface DocumentGenerator {
    Mono<byte[]> generateDocument();
}
