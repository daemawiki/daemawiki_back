package org.daemawiki.domain.user.usecase;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface UploadUserProfileImageUsecase {
    Mono<Void> upload(FilePart filePart);

}
