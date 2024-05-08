package org.daemawiki.domain.file.port;

import org.daemawiki.domain.file.model.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FindFilePort {
    Mono<File> findById(UUID id);
    Flux<File> findByName(String fileName);
    Flux<File> findAll();

}
