package org.daemawiki.domain.file.service;

import org.daemawiki.domain.file.port.FindFilePort;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.usecase.GetFileUsecase;
import org.daemawiki.exception.h404.FileNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetFileService implements GetFileUsecase {
    private final FindFilePort findFilePort;

    public GetFileService(FindFilePort findFilePort) {
        this.findFilePort = findFilePort;
    }

    @Override
    public Mono<File> getFileById(String id) {
        return findFilePort.findById(UUID.fromString(id))
                .switchIfEmpty(Mono.defer(() -> Mono.error(FileNotFoundException.EXCEPTION)));
    }

    @Override
    public Flux<File> getFileByName(String fileName) {
        return findFilePort.findByName(fileName);
    }

    @Override
    public Flux<File> getAll() {
        return findFilePort.findAll();
    }

}
