package org.daemawiki.domain.revision.api;

import jakarta.validation.constraints.NotBlank;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.usecase.GetRevisionUsecase;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/revisions")
public class RevisionHistoryController {
    private final GetRevisionUsecase getRevisionUsecase;

    public RevisionHistoryController(GetRevisionUsecase getRevisionUsecase) {
        this.getRevisionUsecase = getRevisionUsecase;
    }

    @GetMapping("/top-10")
    public Flux<SimpleDocumentResponse> getRevisionTop10ByUpdatedDate() {
        return getRevisionUsecase.getUpdatedTop10Revision();
    }

    @GetMapping("/page")
    public Flux<RevisionHistory> getRevisionToPage(@RequestParam("lastRevision") String lastRevision) {
        return getRevisionUsecase.getAllRevisionPaging(lastRevision);
    }

    @GetMapping("/document/{documentId}")
    public Flux<RevisionHistory> getRevisionByDocument(@NotBlank @PathVariable String documentId, @RequestParam("lastRevision") String lastRevision) {
        return getRevisionUsecase.getAllRevisionByDocument(documentId, lastRevision);
    }

    @GetMapping("/user/{userId}")
    public Flux<RevisionHistory> getRevisionByUser(@NotBlank @PathVariable String userId, @RequestParam("lastRevision") String lastRevision) {
        return getRevisionUsecase.getAllRevisionByUser(userId, lastRevision);
    }

}
