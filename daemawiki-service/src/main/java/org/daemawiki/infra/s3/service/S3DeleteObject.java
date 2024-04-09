package org.daemawiki.infra.s3.service;

import reactor.core.publisher.Mono;

public interface S3DeleteObject {
    Mono<Void> deleteObject(String fileId);

}
