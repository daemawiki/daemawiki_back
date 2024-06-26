package org.daemawiki.domain.file.adapter;

import org.daemawiki.domain.file.port.DeleteFilePort;
import org.daemawiki.domain.file.port.FindFilePort;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.repository.FileRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class FileAdapter implements FindFilePort, DeleteFilePort {
    private final FileRepository fileRepository;

    public FileAdapter(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public Mono<File> findById(UUID id) {
        return fileRepository.findById(id);
    }

    @Override
    public Flux<File> findByName(String fileName) {
        return fileRepository.findAllByFileNameContainingIgnoreCase(fileName);
    }

    @Override
    public Flux<File> findAll() {
        return fileRepository.findAll();
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return fileRepository.deleteById(id);
    }

}
