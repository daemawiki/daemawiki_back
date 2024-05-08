package org.daemawiki.domain.file.port;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteFilePort {
    Mono<Void> deleteById(UUID id);

}
