package com.example.daemawiki.domain.editor.api;

import com.example.daemawiki.domain.editor.dto.AddEditorRequest;
import com.example.daemawiki.domain.editor.dto.DeleteEditorRequest;
import com.example.daemawiki.domain.editor.service.AddEditor;
import com.example.daemawiki.domain.editor.service.DeleteEditor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/editor")
public class DocumentEditorController {
    private final AddEditor addEditorService;
    private final DeleteEditor deleteEditorService;

    public DocumentEditorController(AddEditor addEditorService, DeleteEditor deleteEditorService) {
        this.addEditorService = addEditorService;
        this.deleteEditorService = deleteEditorService;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> addEditor(@RequestBody AddEditorRequest request) {
        return addEditorService.execute(request);
    }

    @PatchMapping("/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteEditor(@RequestBody DeleteEditorRequest request) {
        return deleteEditorService.execute(request);
    }

}
