package com.example.daemawiki.domain.editor.api;

import com.example.daemawiki.domain.editor.dto.AddEditorRequest;
import com.example.daemawiki.domain.editor.dto.DeleteEditorRequest;
import com.example.daemawiki.domain.editor.service.AddEditor;
import com.example.daemawiki.domain.editor.service.DeleteEditor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/{documentId}/editor")
public class DocumentEditorController {
    private final AddEditor addEditorService;
    private final DeleteEditor deleteEditorService;

    public DocumentEditorController(AddEditor addEditorService, DeleteEditor deleteEditorService) {
        this.addEditorService = addEditorService;
        this.deleteEditorService = deleteEditorService;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addEditor(@Valid @RequestBody AddEditorRequest request, @NotBlank @PathVariable String documentId) {
        return addEditorService.execute(request, documentId);
    }

    @PatchMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEditor(@Valid @RequestBody DeleteEditorRequest request, @NotBlank @PathVariable String documentId) {
        return deleteEditorService.execute(request, documentId);
    }

}
