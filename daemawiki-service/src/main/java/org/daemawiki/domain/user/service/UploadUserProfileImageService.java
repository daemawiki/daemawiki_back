package org.daemawiki.domain.user.service;

import org.daemawiki.domain.file.model.type.FileType;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.application.SaveUserPort;
import org.daemawiki.domain.user.usecase.UploadUserProfileImageUsecase;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.FileUploadFailedException;
import org.daemawiki.infra.s3.service.S3UploadObject;
import org.daemawiki.infra.s3.service.impl.S3UploadObjectImpl;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UploadUserProfileImageService implements UploadUserProfileImageUsecase {
    private final FindUserPort findUserPort;
    private final S3UploadObject s3UploadObject;
    private final SaveUserPort saveUserPort;

    public UploadUserProfileImageService(FindUserPort findUserPort, S3UploadObjectImpl s3UploadObject, SaveUserPort saveUserPort) {
        this.findUserPort = findUserPort;
        this.s3UploadObject = s3UploadObject;
        this.saveUserPort = saveUserPort;
    }

    @Override
    public Mono<Void> upload(FilePart filePart) {
        return findUserPort.currentUser()
                .zipWith(s3UploadObject.uploadObject(filePart, FileType.PROFILE.toString()), (user, file) -> {
                    user.setProfile(file);
                    return user;
                })
                .flatMap(saveUserPort::save)
                .onErrorMap(e -> e instanceof FileUploadFailedException ? e : ExecuteFailedException.EXCEPTION)
                .then();
    }

}
