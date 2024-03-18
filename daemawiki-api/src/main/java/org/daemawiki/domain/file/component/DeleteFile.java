package org.daemawiki.domain.file.component;

import org.daemawiki.domain.file.repository.FileRepository;
import org.daemawiki.exception.h500.ExecuteFailedException;
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
        return fileRepository.deleteById(UUID.fromString(id))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
