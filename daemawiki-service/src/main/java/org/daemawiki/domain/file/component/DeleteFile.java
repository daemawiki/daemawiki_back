package org.daemawiki.domain.file.component;

import org.daemawiki.domain.file.port.DeleteFilePort;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class DeleteFile {
    private final DeleteFilePort deleteFilePort;

    public DeleteFile(DeleteFilePort deleteFilePort) {
        this.deleteFilePort = deleteFilePort;
    }

    public Mono<Void> deleteById(String id) {
        return deleteFilePort.deleteById(UUID.fromString(id))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
