package org.daemawiki.domain.file.component.service;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.repository.FileRepository;
import org.daemawiki.exception.h404.FileNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class GetFile {
    private final FileRepository fileRepository;

    public GetFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Mono<File> getFileById(String id) {
        return fileRepository.findById(UUID.fromString(id))
                .switchIfEmpty(Mono.error(FileNotFoundException.EXCEPTION));
    }

    public Flux<File> getFileByName(String fileName) {
        return fileRepository.findAllByFileNameContainingIgnoreCase(fileName);
    }

    public Flux<File> getAll() {
        return fileRepository.findAll();
    }

}
