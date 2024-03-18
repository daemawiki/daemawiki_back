package org.daemawiki.domain.document.api;

import org.daemawiki.domain.document.component.service.CreateDocument;
import org.daemawiki.domain.document.component.service.DeleteDocument;
import org.daemawiki.domain.document.component.service.GetDocument;
import org.daemawiki.domain.document.component.service.UpdateDocument;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.info.dto.UpdateInfoRequest;
import org.daemawiki.domain.info.service.DocumentImageUpload;
import org.daemawiki.domain.info.service.UpdateInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final CreateDocument createDocumentService;
    private final GetDocument getDocumentService;
    private final DeleteDocument deleteDocumentService;
    private final UpdateDocument updateDocumentService;
    private final UpdateInfo updateInfoService;
    private final DocumentImageUpload documentImageUpload;

    public DocumentController(CreateDocument createDocument, GetDocument getDocument, DeleteDocument deleteDocument, UpdateDocument updateDocument, UpdateInfo updateInfoService, DocumentImageUpload documentImageUpload) {
        this.createDocumentService = createDocument;
        this.getDocumentService = getDocument;
        this.deleteDocumentService = deleteDocument;
        this.updateDocumentService = updateDocument;
        this.updateInfoService = updateInfoService;
        this.documentImageUpload = documentImageUpload;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createDocument(@Valid @RequestBody SaveDocumentRequest request) {
        return createDocumentService.execute(request);
    }

    @GetMapping("/{documentId}")
    public Mono<GetDocumentResponse> getDocument(@NotBlank @PathVariable String documentId) {
        return getDocumentService.getDocumentById(documentId);
    }

    @GetMapping("/random")
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return getDocumentService.getDocumentByRandom();
    }

    @GetMapping("/search")
    public Flux<DocumentSearchResult> searchDocument(@NotBlank @RequestParam("text") String text) {
        return getDocumentService.searchDocument(text);
    }

    @GetMapping("/most-revision/top-10")
    public Flux<SimpleDocumentResponse> getDocumentOrderByVersion() {
        return getDocumentService.getDocumentTop10();
    }

    @GetMapping("/most-view")
    public Flux<SimpleDocumentResponse> getDocumentOrderByView() {
        return getDocumentService.getDocumentOrderByView();
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteDocument(@NotBlank @PathVariable String documentId) {
        return deleteDocumentService.execute(documentId);
    }

    @PatchMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateDocument(@NotBlank @PathVariable String documentId, @Valid @RequestBody SaveDocumentRequest request) {
        return updateDocumentService.execute(request, documentId);
    }

    @PatchMapping("/info")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateInfo(@Valid @RequestBody UpdateInfoRequest request) {
        return updateInfoService.execute(request);
    }

    @PatchMapping(value = "/{documentId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateImage(@PathVariable String documentId, @RequestPart(value = "file", required = true) FilePart filePart) {
        return documentImageUpload.execute(filePart, documentId);
    }

}
