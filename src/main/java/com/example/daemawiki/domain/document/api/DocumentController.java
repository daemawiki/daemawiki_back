package com.example.daemawiki.domain.document.api;

import com.example.daemawiki.domain.document.dto.request.CreateDocumentRequest;
import com.example.daemawiki.domain.document.dto.request.UpdateDocumentRequest;
import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.service.CreateDocument;
import com.example.daemawiki.domain.document.service.DeleteDocument;
import com.example.daemawiki.domain.document.service.GetDocument;
import com.example.daemawiki.domain.document.service.UpdateDocument;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final CreateDocument createDocumentService;
    private final GetDocument getDocumentService;
    private final DeleteDocument deleteDocumentService;
    private final UpdateDocument updateDocumentService;

    public DocumentController(CreateDocument createDocument, GetDocument getDocument, DeleteDocument deleteDocument, UpdateDocument updateDocument) {
        this.createDocumentService = createDocument;
        this.getDocumentService = getDocument;
        this.deleteDocumentService = deleteDocument;
        this.updateDocumentService = updateDocument;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        return createDocumentService.execute(request);
    }

    @GetMapping("/{documentId}")
    public Mono<GetDocumentResponse> getDocument(@PathVariable String documentId) {
        return getDocumentService.execute(documentId);
    }

    @DeleteMapping("/{documentId}")
    public Mono<Void> deleteDocument(@PathVariable String documentId) {
        return deleteDocumentService.execute(documentId);
    }

    @PatchMapping
    public Mono<Void> updateDocument(@RequestBody UpdateDocumentRequest request) {
        return updateDocumentService.execute(request);
    }

}
