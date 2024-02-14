package com.example.daemawiki.infra.s3;

import com.example.daemawiki.infra.s3.model.FileDetail;
import com.example.daemawiki.infra.s3.model.FileResponse;
import com.example.daemawiki.infra.s3.model.type.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final S3AsyncClient s3AsyncClient;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public Mono<FileResponse> uploadObject(FilePart filePart, String imageType) {
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
                                        });
                            });
                })
                .flatMap(response -> Mono.just(FileResponse.builder()
                        .fileName(filename)
                        .fileType(type.toString())
                        .detail(FileDetail.builder()
                                .type(switch (imageType) {
                                    case "content" -> FileType.CONTENT;
                                    case "profile" -> FileType.PROFILE;
                                    case null, default -> FileType.OTHER;
                                })
                                .url("https://" + bucket + ".s3.amazonaws.com/" + filename)
                                .build())
                        .build()));
    }

    public Mono<Void> deleteObject(String key) {
        return Mono.just(DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build())
                .map(s3AsyncClient::deleteObject)
                .flatMap(Mono::fromFuture)
                .then();
    }

}