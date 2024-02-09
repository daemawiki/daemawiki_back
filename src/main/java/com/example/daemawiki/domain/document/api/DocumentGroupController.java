package com.example.daemawiki.domain.document.api;

import com.example.daemawiki.domain.document.dto.request.AddDocumentGroupRequest;
import com.example.daemawiki.domain.document.dto.request.DeleteDocumentGroupRequest;
import com.example.daemawiki.domain.document.service.group.AddDocumentGroup;
import com.example.daemawiki.domain.document.service.group.DeleteDocumentGroup;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/documents/groups")
public class DocumentGroupController {
    private final AddDocumentGroup addDocumentGroup;
    private final DeleteDocumentGroup deleteDocumentGroup;

    public DocumentGroupController(AddDocumentGroup addDocumentGroup, DeleteDocumentGroup deleteDocumentGroup) {
        this.addDocumentGroup = addDocumentGroup;
        this.deleteDocumentGroup = deleteDocumentGroup;
    }

    @PostMapping
    public Mono<Void> addGroup(@RequestBody AddDocumentGroupRequest request) {
        return addDocumentGroup.execute(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGroup(@RequestBody DeleteDocumentGroupRequest request) {
        return deleteDocumentGroup.execute(request);
    }

}
