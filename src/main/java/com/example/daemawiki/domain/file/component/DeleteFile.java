package com.example.daemawiki.domain.file.component;

import com.example.daemawiki.domain.file.repository.FileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class DeleteFile {
    private final FileRepository fileRepository;

    public DeleteFile(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Mono<Void> deleteById(String id) {
        return fileRepository.deleteById(UUID.fromString(id));
    }

}
