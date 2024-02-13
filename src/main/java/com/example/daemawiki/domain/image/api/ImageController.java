package com.example.daemawiki.domain.image.api;

import com.example.daemawiki.infra.s3.S3Service;
import com.example.daemawiki.infra.s3.model.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final S3Service service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ImageResponse> imgaeUpload(@RequestPart("file") FilePart filePart) {
        return service.uploadObject(filePart);
    }

}
