package org.daemawiki.infra.s3.service.impl;

import org.daemawiki.domain.file.component.DeleteFile;
import org.daemawiki.domain.file.dto.DeleteFileRequest;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.infra.s3.service.S3DeleteObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3DeleteObjectImpl implements S3DeleteObject {
    private final S3AsyncClient s3AsyncClient;
    private final DeleteFile deleteFile;

    public S3DeleteObjectImpl(S3AsyncClient s3AsyncClient, DeleteFile deleteFile) {
        this.s3AsyncClient = s3AsyncClient;
        this.deleteFile = deleteFile;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public Mono<Void> deleteObject(DeleteFileRequest request) {
        return Mono.just(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(request.key())
                        .build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .flatMap(deleteObjectResponse -> deleteFile.deleteById(request.key()))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
