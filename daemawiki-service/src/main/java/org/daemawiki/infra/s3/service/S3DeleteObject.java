package org.daemawiki.infra.s3.service;

import org.daemawiki.domain.file.dto.DeleteFileRequest;
import reactor.core.publisher.Mono;

public interface S3DeleteObject {
    Mono<Void> deleteObject(DeleteFileRequest request);

}
