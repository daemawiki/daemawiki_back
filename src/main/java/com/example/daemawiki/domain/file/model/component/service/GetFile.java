package com.example.daemawiki.domain.file.model.component.service;

import com.example.daemawiki.domain.file.model.File;
import com.example.daemawiki.domain.file.repository.FileRepository;
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
        return fileRepository.findById(UUID.fromString(id));
    }

}
