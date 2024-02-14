package com.example.daemawiki.domain.file.api;

import com.example.daemawiki.domain.file.dto.DeleteFileRequest;
import com.example.daemawiki.infra.s3.S3DeleteObject;
import com.example.daemawiki.infra.s3.S3UploadObject;
import com.example.daemawiki.infra.s3.model.FileResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final S3UploadObject s3UploadObject;
    private final S3DeleteObject s3DeleteObject;

    public FileController(S3UploadObject s3UploadObject, S3DeleteObject s3DeleteObject) {
        this.s3UploadObject = s3UploadObject;
        this.s3DeleteObject = s3DeleteObject;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<FileResponse> uploadFile(@RequestPart(value = "file", required = true) FilePart filePart, @RequestParam("type") String fileType) {
        return s3UploadObject.uploadObject(filePart, fileType);
    }

    @DeleteMapping
    public Mono<Void> deleteFile(@Valid @RequestBody DeleteFileRequest request) {
        return s3DeleteObject.deleteObject(request);
    }

}
