package org.daemawiki.domain.file.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.daemawiki.domain.file.dto.DeleteFileRequest;
import org.daemawiki.domain.file.model.File;
import org.daemawiki.domain.file.usecase.GetFileUsecase;
import org.daemawiki.infra.s3.service.S3DeleteObject;
import org.daemawiki.infra.s3.service.S3UploadObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final S3UploadObject s3UploadObject;
    private final S3DeleteObject s3DeleteObject;
    private final GetFileUsecase getFileUsecase;

    public FileController(S3UploadObject s3UploadObject, S3DeleteObject s3DeleteObject, GetFileUsecase getFileUsecase) {
        this.s3UploadObject = s3UploadObject;
        this.s3DeleteObject = s3DeleteObject;
        this.getFileUsecase = getFileUsecase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<File> uploadFile(@RequestPart(value = "file", required = true) FilePart filePart, @NotBlank @RequestParam("type") String fileType) {
        return s3UploadObject.uploadObject(filePart, fileType);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteFile(@Valid @RequestBody DeleteFileRequest request) {
        return s3DeleteObject.deleteObject(request);
    }

    @GetMapping("/{fileId}")
    public Mono<File> getFileById(@NotBlank @PathVariable String fileId) {
        return getFileUsecase.getFileById(fileId);
    }

    @GetMapping
    public Flux<File> getFileByName(@NotBlank @RequestParam String name) {
        return getFileUsecase.getFileByName(name);
    }

    @GetMapping("/list")
    public Flux<File> getAll() {
        return getFileUsecase.getAll();
    }

}
