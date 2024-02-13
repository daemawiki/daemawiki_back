package com.example.daemawiki.domain.revision.api;

import com.example.daemawiki.domain.revision.dto.GetRevisionPageRequest;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.service.RevisionService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/revision")
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
    public Flux<RevisionHistory> getRevisionToPage(@RequestBody GetRevisionPageRequest request) {
        return revisionService.getAllRevisionPaging(request);
    }

}
