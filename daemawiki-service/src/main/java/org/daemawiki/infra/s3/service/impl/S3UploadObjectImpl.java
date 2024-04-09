package org.daemawiki.infra.s3.service.impl;

import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.model.FileDetail;
import org.daemawiki.domain.file.model.type.FileType;
import org.daemawiki.domain.file.repository.FileRepository;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.FileUploadFailedException;
import org.daemawiki.infra.s3.config.AwsS3Properties;
import org.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CompleteMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.CreateMultipartUploadRequest;
import software.amazon.awssdk.services.s3.model.UploadPartRequest;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class S3UploadObjectImpl implements S3UploadObject {
    private final S3AsyncClient s3AsyncClient;
    private final FileRepository fileRepository;
    private final AwsS3Properties awsS3Properties;

    public S3UploadObjectImpl(S3AsyncClient s3AsyncClient, FileRepository fileRepository, AwsS3Properties awsS3Properties) {
        this.s3AsyncClient = s3AsyncClient;
        this.fileRepository = fileRepository;
        this.awsS3Properties = awsS3Properties;
    }

    @Override
    public Mono<File> uploadObject(FilePart filePart, String fileType) {
        String filename = URLEncoder.encode(filePart.filename(), StandardCharsets.UTF_8);
        final UUID key = UUID.randomUUID();
        final String keyString = key.toString();
        Map<String, String> metadata = Map.of("filename", filename);
        Optional<MediaType> type = Optional.ofNullable(filePart.headers().getContentType());
        final String bucket = awsS3Properties.getBucket();

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(keyString)
                .metadata(metadata)
                .build();

        return Mono.fromCompletionStage(s3AsyncClient.createMultipartUpload(createRequest))
                .flatMap(uploadResponse -> {
                    String uploadId = uploadResponse.uploadId();
                    int partNumber = 1;

                    return DataBufferUtils.join(filePart.content())
                            .flatMap(dataBuffer -> {
                                byte[] fileContent = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(fileContent);
                                DataBufferUtils.release(dataBuffer);

                                    return Mono.fromCompletionStage(s3AsyncClient.uploadPart(UploadPartRequest.builder()
                                                .bucket(bucket)
                                                .key(keyString)
                                                .partNumber(partNumber)
                                                .uploadId(uploadId)
                                                .build(), AsyncRequestBody.fromBytes(fileContent)))
                                        .map(uploadPartResponse -> {
                                            CompletedPart part = CompletedPart.builder()
                                                    .partNumber(partNumber)
                                                    .eTag(uploadPartResponse.eTag())
                                                    .build();

                                            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                                                    .bucket(bucket)
                                                    .key(keyString)
                                                    .uploadId(uploadId)
                                                    .multipartUpload(completedMultipartUpload -> completedMultipartUpload.parts(Collections.singletonList(part)))
                                                    .build();

                                            return s3AsyncClient.completeMultipartUpload(completeRequest);
                                        })
                                        .onErrorMap(e -> FileUploadFailedException.EXCEPTION);
                            });
                })
                .flatMap(response -> createFile(key, URLDecoder.decode(filename), type.orElse(null), fileType.toLowerCase()))
                .onErrorMap(e -> e instanceof FileUploadFailedException ? e : ExecuteFailedException.EXCEPTION);
    }

    private Mono<File> createFile(UUID key, String fileName, MediaType mediaType, String filetype) {
        return Mono.just(File.create(key,
                        fileName,
                        mediaType != null ? mediaType.toString() : "null",
                        FileDetail.create(
                                FileType.valueOf(filetype.toUpperCase()),
                                String.format("https://%s.s3.amazonaws.com/%s", awsS3Properties.getBucket(), key))))
                .flatMap(fileRepository::save);
    }

}