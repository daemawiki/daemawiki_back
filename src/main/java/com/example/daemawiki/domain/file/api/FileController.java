package com.example.daemawiki.domain.file.api;

import com.example.daemawiki.domain.file.dto.DeleteFileRequest;
import com.example.daemawiki.infra.s3.S3Service;
import com.example.daemawiki.infra.s3.model.FileResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/image")
public class FileController {
    private final S3Service service;

    public FileController(S3Service service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileResponse> uploadFile(@RequestPart("file") FilePart filePart, @RequestParam("type") String imageType) {
        return service.uploadObject(filePart, imageType);
    }

    @DeleteMapping
    public Mono<Void> deleteFile(@Valid @RequestBody DeleteFileRequest request) {
        return service.deleteObject(request);
    }

}
