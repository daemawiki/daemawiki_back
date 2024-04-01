package org.daemawiki.domain.info.usecase;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface UploadDocumentImageUsecase {
    Mono<Void> upload(FilePart filePart, String documentId);

}
