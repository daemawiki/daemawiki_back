package com.example.daemawiki.domain.document.api;

import com.example.daemawiki.domain.content.dto.AddContentRequest;
import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.service.AddContentTable;
import com.example.daemawiki.domain.content.service.WriteContent;
import com.example.daemawiki.domain.document.component.service.CreateDocument;
import com.example.daemawiki.domain.document.component.service.DeleteDocument;
import com.example.daemawiki.domain.document.component.service.GetDocument;
import com.example.daemawiki.domain.document.component.service.UpdateDocument;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.dto.response.SearchDocumentResponse;
import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.editor.dto.AddEditorRequest;
import com.example.daemawiki.domain.editor.service.AddEditor;
import com.example.daemawiki.domain.info.dto.UpdateInfoRequest;
import com.example.daemawiki.domain.info.service.UpdateInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
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
    private final WriteContent writeContentService;
    private final AddContentTable addContentTableService;
    private final AddEditor addEditorService;

    public DocumentController(CreateDocument createDocument, GetDocument getDocument, DeleteDocument deleteDocument, UpdateDocument updateDocument, UpdateInfo updateInfoService, WriteContent writeContentService, AddContentTable addContentTableService, AddEditor addEditorService) {
        this.createDocumentService = createDocument;
        this.getDocumentService = getDocument;
        this.deleteDocumentService = deleteDocument;
        this.updateDocumentService = updateDocument;
        this.updateInfoService = updateInfoService;
        this.writeContentService = writeContentService;
        this.addContentTableService = addContentTableService;
        this.addEditorService = addEditorService;
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
    public Flux<SearchDocumentResponse> searchDocument(@NotBlank @RequestParam("text") String text) {
        return getDocumentService.searchDocument(text);
    }

    @GetMapping("/most-revision/top-10")
    public Flux<SimpleDocumentResponse> getDocumentOrderByVersion() {
        return getDocumentService.getDocumentTop10();
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
    public Mono<Void> updateInfo(@RequestBody UpdateInfoRequest request) {
        return updateInfoService.execute(request);
    }

    @PatchMapping("/content")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> writeContent(@RequestBody WriteContentRequest request) {
        return writeContentService.execute(request);
    }

    @PatchMapping("/content/table")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addContentTable(@RequestBody AddContentRequest request) {
        return addContentTableService.execute(request);
    }

    @PatchMapping("/editor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addEditor(@RequestBody AddEditorRequest request) {
        return addEditorService.execute(request);
    }

}
