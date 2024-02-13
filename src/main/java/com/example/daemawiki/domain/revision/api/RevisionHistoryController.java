package com.example.daemawiki.domain.revision.api;

import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.service.RevisionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

}
