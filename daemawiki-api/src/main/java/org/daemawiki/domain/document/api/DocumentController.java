package org.daemawiki.domain.document.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.usecase.CreateDocumentUsecase;
import org.daemawiki.domain.document.usecase.DeleteDocumentUsecase;
import org.daemawiki.domain.document.usecase.GetDocumentUsecase;
import org.daemawiki.domain.document.usecase.UpdateDocumentUsecase;
import org.daemawiki.domain.info.dto.UpdateInfoRequest;
import org.daemawiki.domain.info.usecase.UpdateDocumentInfoUsecase;
import org.daemawiki.domain.info.usecase.UploadDocumentImageUsecase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final CreateDocumentUsecase createDocumentUsecase;
    private final DeleteDocumentUsecase deleteDocumentUsecase;
    private final GetDocumentUsecase getDocumentUsecase;
    private final UpdateDocumentUsecase updateDocumentUsecase;
    private final UpdateDocumentInfoUsecase updateDocumentInfoUsecase;
    private final UploadDocumentImageUsecase uploadDocumentImageUsecase;

    public DocumentController(CreateDocumentUsecase createDocumentUsecase, DeleteDocumentUsecase deleteDocumentUsecase, GetDocumentUsecase getDocumentUsecase, UpdateDocumentUsecase updateDocumentUsecase, UpdateDocumentInfoUsecase updateDocumentInfoUsecase, UploadDocumentImageUsecase uploadDocumentImageUsecase) {
        this.createDocumentUsecase = createDocumentUsecase;
        this.deleteDocumentUsecase = deleteDocumentUsecase;
        this.getDocumentUsecase = getDocumentUsecase;
        this.updateDocumentUsecase = updateDocumentUsecase;
        this.updateDocumentInfoUsecase = updateDocumentInfoUsecase;
        this.uploadDocumentImageUsecase = uploadDocumentImageUsecase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createDocument(@Valid @RequestBody SaveDocumentRequest request) {
        return createDocumentUsecase.create(request);
    }

    @GetMapping("/{documentId}")
    public Mono<GetDocumentResponse> getDocument(@NotBlank @PathVariable String documentId) {
        return getDocumentUsecase.getDocumentById(documentId);
    }

    @GetMapping("/random")
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return getDocumentUsecase.getDocumentByRandom();
    }

    @GetMapping("/search")
    public Flux<DocumentSearchResult> searchDocument(@NotBlank @RequestParam("text") String text) {
        return getDocumentUsecase.searchDocument(text);
    }

    @GetMapping("/search/title")
    public Flux<DocumentSearchResult> searchDocumentTitle(@NotBlank @RequestParam("text") String text) {
        return getDocumentUsecase.searchDocumentTitle(text);
    }

    @GetMapping("/search/content")
    public Flux<DocumentSearchResult> searchDocumentContent(@NotBlank @RequestParam("text") String text) {
        return getDocumentUsecase.searchDocumentContent(text);
    }

    @GetMapping("/most-revision/top-10")
    public Flux<SimpleDocumentResponse> getDocumentOrderByVersion() {
        return getDocumentUsecase.getDocumentTop10();
    }

    @GetMapping
    public Flux<SimpleDocumentResponse> getDocumentOrderByView() {
        return getDocumentUsecase.getDocumentOrderByView();
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDocument(@NotBlank @PathVariable String documentId) {
        return deleteDocumentUsecase.delete(documentId);
    }

    @PatchMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateDocument(@NotBlank @PathVariable String documentId, @Valid @RequestBody SaveDocumentRequest request) {
        return updateDocumentUsecase.update(request, documentId);
    }

    @PatchMapping("/{documentId}/info")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateInfo(@Valid @RequestBody UpdateInfoRequest request, @NotBlank @PathVariable String documentId) {
        return updateDocumentInfoUsecase.update(documentId, request);
    }

    @PatchMapping(value = "/{documentId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateImage(@PathVariable String documentId, @RequestPart(value = "file", required = true) FilePart filePart) {
        return uploadDocumentImageUsecase.upload(filePart, documentId);
    }

}
