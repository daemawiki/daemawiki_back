package org.daemawiki.domain.file.usecase;

import org.daemawiki.domain.file.model.File;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetFileUsecase {
    Mono<File> getFileById(String id);
    Flux<File> getFileByName(String fileName);
    Flux<File> getAll();

}
