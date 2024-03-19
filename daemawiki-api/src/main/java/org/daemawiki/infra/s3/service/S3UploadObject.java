package org.daemawiki.infra.s3.service;

import org.daemawiki.domain.file.model.File;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

public interface S3UploadObject {
    Mono<File> uploadObject(FilePart filePart, String fileType);

}
