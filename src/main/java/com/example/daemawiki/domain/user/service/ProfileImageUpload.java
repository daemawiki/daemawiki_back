package com.example.daemawiki.domain.user.service;

import com.example.daemawiki.domain.file.model.type.FileType;
import com.example.daemawiki.domain.user.repository.UserRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import com.example.daemawiki.global.exception.h500.FileUploadFailedException;
import com.example.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProfileImageUpload {
    private final UserFacade userFacade;
    private final S3UploadObject s3UploadObject;
    private final UserRepository userRepository;

    public ProfileImageUpload(UserFacade userFacade, S3UploadObject s3UploadObject, UserRepository userRepository) {
        this.userFacade = userFacade;
        this.s3UploadObject = s3UploadObject;
        this.userRepository = userRepository;
    }

    public Mono<Void> execute(FilePart filePart) {
        return userFacade.currentUser()
                .zipWith(s3UploadObject.uploadObject(filePart, FileType.PROFILE.toString()), (user, file) -> {
                    user.setProfile(file);
                    return user;
                })
                .flatMap(userRepository::save)
                .onErrorMap(e -> e instanceof FileUploadFailedException ? e : ExecuteFailedException.EXCEPTION)
                .then();
    }

}
