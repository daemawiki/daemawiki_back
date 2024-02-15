package com.example.daemawiki.domain.revision.api;

import com.example.daemawiki.domain.revision.dto.GetRevisionPageRequest;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.component.service.RevisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/revisions")
public class RevisionHistoryController {
    private final RevisionService revisionService;

    public RevisionHistoryController(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    @GetMapping
    public Flux<RevisionHistory> getRevisionTop10ByUpdatedDate() {
        return revisionService.getUpdatedTop10Revision();
    }

    @GetMapping("/page")
    public Flux<RevisionHistory> getRevisionToPage(@Valid @RequestBody GetRevisionPageRequest request) {
        return revisionService.getAllRevisionPaging(request);
    }

    @GetMapping("/{documentId}")
    public Flux<RevisionHistory> getRevisionByDocument(@PathVariable String documentId, @Valid @RequestBody GetRevisionPageRequest request) {
        return revisionService.getAllRevisionByDocument(documentId, request);
    }

}
