package org.daemawiki.domain.document_editor.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.daemawiki.domain.document_editor.dto.AddEditorRequest;
import org.daemawiki.domain.document_editor.dto.DeleteEditorRequest;
import org.daemawiki.domain.document_editor.usecase.AddDocumentEditorUsecase;
import org.daemawiki.domain.document_editor.usecase.RemoveDocumentEditorUsecase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/{documentId}/editor")
public class DocumentEditorController {
    private final RemoveDocumentEditorUsecase removeDocumentEditorUsecase;
    private final AddDocumentEditorUsecase addDocumentEditorUsecase;

    public DocumentEditorController(RemoveDocumentEditorUsecase removeDocumentEditorUsecase, AddDocumentEditorUsecase addDocumentEditorUsecase) {
        this.removeDocumentEditorUsecase = removeDocumentEditorUsecase;
        this.addDocumentEditorUsecase = addDocumentEditorUsecase;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addEditor(@Valid @RequestBody AddEditorRequest request, @NotBlank @PathVariable String documentId) {
        return addDocumentEditorUsecase.add(request, documentId);
    }

    @PatchMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEditor(@Valid @RequestBody DeleteEditorRequest request, @NotBlank @PathVariable String documentId) {
        return removeDocumentEditorUsecase.remove(request, documentId);
    }

}
