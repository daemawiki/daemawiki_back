package com.example.daemawiki.infra.s3.service;

import com.example.daemawiki.domain.file.dto.DeleteFileRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Service
public class S3DeleteObject {
    private final S3AsyncClient s3AsyncClient;

    public S3DeleteObject(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Mono<Void> deleteObject(DeleteFileRequest request) {
        return Mono.just(DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(request.key())
                        .build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .then();
    }

}
