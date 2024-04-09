package org.daemawiki.infra.s3.service.impl;

import org.daemawiki.domain.file.component.DeleteFile;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.infra.s3.config.AwsS3Properties;
import org.daemawiki.infra.s3.service.S3DeleteObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3DeleteObjectImpl implements S3DeleteObject {
    private final S3AsyncClient s3AsyncClient;
    private final DeleteFile deleteFile;
    private final AwsS3Properties awsS3Properties;

    public S3DeleteObjectImpl(S3AsyncClient s3AsyncClient, DeleteFile deleteFile, AwsS3Properties awsS3Properties) {
        this.s3AsyncClient = s3AsyncClient;
        this.deleteFile = deleteFile;
        this.awsS3Properties = awsS3Properties;
    }

    @Override
    public Mono<Void> deleteObject(String fileId) {
        return Mono.just(DeleteObjectRequest.builder()
                        .bucket(awsS3Properties.getBucket())
                        .key(fileId)
                        .build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .flatMap(deleteObjectResponse -> deleteFile.deleteById(fileId))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
