package org.daemawiki.domain.file.usecase.service;

import org.daemawiki.domain.file.application.GetFilePort;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.usecase.GetFileUsecase;
import org.daemawiki.exception.h404.FileNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetFileService implements GetFileUsecase {
    private final GetFilePort getFilePort;

    public GetFileService(GetFilePort getFilePort) {
        this.getFilePort = getFilePort;
    }

    @Override
    public Mono<File> getFileById(String id) {
        return getFilePort.findById(UUID.fromString(id))
                .switchIfEmpty(Mono.error(FileNotFoundException.EXCEPTION));
    }

    @Override
    public Flux<File> getFileByName(String fileName) {
        return getFilePort.findByName(fileName);
    }

    @Override
    public Flux<File> getAll() {
        return getFilePort.findAll();
    }

}
