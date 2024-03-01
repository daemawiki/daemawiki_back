package com.example.daemawiki.domain.revision.api;

import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.revision.component.service.RevisionService;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/revisions")
public class RevisionHistoryController {
    private final RevisionService revisionService;

    public RevisionHistoryController(RevisionService revisionService) {
        this.revisionService = revisionService;
    }

    @GetMapping("/top-10")
    public Flux<SimpleDocumentResponse> getRevisionTop10ByUpdatedDate() {
        return revisionService.getUpdatedTop10Revision();
    }

    @GetMapping("/page")
    public Flux<RevisionHistory> getRevisionToPage(@RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionPaging(lastRevision);
    }

    @GetMapping("/document/{documentId}")
    public Flux<RevisionHistory> getRevisionByDocument(@NotBlank @PathVariable String documentId, @RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionByDocument(documentId, lastRevision);
    }

    @GetMapping("/user/{userId}")
    public Flux<RevisionHistory> getRevisionByUser(@NotBlank @PathVariable String userId, @RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionByUser(userId, lastRevision);
    }

}
