package com.example.daemawiki.infra.s3.service;

import com.example.daemawiki.domain.file.model.File;
import com.example.daemawiki.domain.file.model.FileDetail;
import com.example.daemawiki.domain.file.model.type.FileType;
import com.example.daemawiki.domain.file.repository.FileRepository;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import com.example.daemawiki.global.exception.h500.FileUploadFailedException;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class S3UploadObject {
    private final S3AsyncClient s3AsyncClient;
    private final FileRepository fileRepository;

    public S3UploadObject(S3AsyncClient s3AsyncClient, FileRepository fileRepository) {
        this.s3AsyncClient = s3AsyncClient;
        this.fileRepository = fileRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Mono<File> uploadObject(FilePart filePart, String fileType) {
        String filename = filePart.filename();
        UUID key = UUID.randomUUID();
        String keyString = key.toString();
        Map<String, String> metadata = Map.of("filename", filename);
        MediaType type = filePart.headers().getContentType();

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(keyString)
                .metadata(metadata)
                .build();

        return Mono.fromCompletionStage(s3AsyncClient.createMultipartUpload(createRequest))
                .flatMap(uploadResponse -> {
                    String uploadId = uploadResponse.uploadId();
                    int partNumber =  1;

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
                .flatMap(response -> createFile(key, filename, type, fileType.toLowerCase()))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private Mono<File> createFile(UUID key, String fileName, MediaType mediaType, String filetype) {
        return Mono.just(File.builder()
                        .id(key)
                        .fileName(fileName)
                        .fileType(mediaType.toString())
                        .detail(FileDetail.builder()
                                .type(switch (filetype) {
                                    case "content" -> FileType.CONTENT;
                                    case "profile" -> FileType.PROFILE;
                                    case null, default -> FileType.OTHER;
                                })
                                .url("https://" + bucket + ".s3.amazonaws.com/" + key)
                                .build())
                        .build())
                .flatMap(fileRepository::save);
    }

}