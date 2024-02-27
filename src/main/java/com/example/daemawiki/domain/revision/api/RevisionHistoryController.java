package com.example.daemawiki.domain.revision.api;

import com.example.daemawiki.domain.revision.component.service.RevisionService;
import com.example.daemawiki.domain.revision.dto.response.RevisionDocumentDetailResponse;
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
    public Flux<RevisionDocumentDetailResponse> getRevisionTop10ByUpdatedDate() {
        return revisionService.getUpdatedTop10Revision();
    }

    @GetMapping("/page")
    public Flux<RevisionHistory> getRevisionToPage(@RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionPaging(lastRevision);
    }

    @GetMapping("/document")
    public Flux<RevisionHistory> getRevisionByDocument(@NotBlank @RequestParam String value, @RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionByDocument(value, lastRevision);
    }

    @GetMapping("/user")
    public Flux<RevisionHistory> getRevisionByUser(@NotBlank @RequestParam String value, @RequestParam("lastRevision") String lastRevision) {
        return revisionService.getAllRevisionByUser(value, lastRevision);
    }

}
