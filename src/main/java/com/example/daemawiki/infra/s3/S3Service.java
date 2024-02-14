package com.example.daemawiki.infra.s3;

import com.example.daemawiki.infra.s3.model.FileDetail;
import com.example.daemawiki.infra.s3.model.FileResponse;
import com.example.daemawiki.infra.s3.model.type.FileType;
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

@Service
public class S3Service {
    private final S3AsyncClient s3AsyncClient;

    public S3Service(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public Mono<FileResponse> uploadObject(FilePart filePart, String fileType) {
        String filename = filePart.filename();
        Map<String, String> metadata = Map.of("filename", filename);
        MediaType type = filePart.headers().getContentType();

        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(filename)
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
                                                .key(filename)
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
                                                    .key(filename)
                                                    .uploadId(uploadId)
                                                    .multipartUpload(completedMultipartUpload -> completedMultipartUpload.parts(Collections.singletonList(part)))
                                                    .build();

                                            return s3AsyncClient.completeMultipartUpload(completeRequest);
                                        })
                                        .onErrorResume(Mono::error);
                            });
                })
                .flatMap(response -> createFileResponse(filename, type, fileType));
    }

    private Mono<FileResponse> createFileResponse(String fileName, MediaType mediaType, String filetype) {
        return Mono.just(FileResponse.builder()
                .fileName(fileName)
                .fileType(mediaType.toString())
                .detail(FileDetail.builder()
                        .type(switch (filetype) {
                            case "content" -> FileType.CONTENT;
                            case "profile" -> FileType.PROFILE;
                            case null, default -> FileType.OTHER;
                        })
                        .url("https://" + bucket + ".s3.amazonaws.com/" + fileName)
                        .build())
                .build());
    }

}