package com.example.daemawiki.domain.file.component.service;

import com.example.daemawiki.domain.file.model.File;
import com.example.daemawiki.domain.file.repository.FileRepository;
import com.example.daemawiki.global.exception.H404.FileNotFoundException;
import org.springframework.stereotype.Service;
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

}
