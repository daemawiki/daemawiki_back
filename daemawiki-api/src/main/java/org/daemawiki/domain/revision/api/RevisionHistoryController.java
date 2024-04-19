package org.daemawiki.domain.revision.api;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.usecase.GetRevisionUsecase;
import org.daemawiki.utils.PagingInfo;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RevisionHistoryController {
    private final GetRevisionUsecase getRevisionUsecase;

    public RevisionHistoryController(GetRevisionUsecase getRevisionUsecase) {
        this.getRevisionUsecase = getRevisionUsecase;
    }

    @GetMapping("/revisions")
    public Flux<RevisionHistory> getRevisionToPage(
            @RequestParam(defaultValue = "createdDateTime") String sortBy,
            @RequestParam(defaultValue = "1") Integer sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return getRevisionUsecase.getAllRevisionPaging(PagingInfo.of(sortBy, sortDirection, page, size));
    }

    @GetMapping("/documents/revisions")
    public Flux<SimpleDocumentResponse> getRevisionOrderByUpdatedDate(
            @RequestParam List<String> types,
            @RequestParam(defaultValue = "createdDateTime") String sortBy,
            @RequestParam(defaultValue = "1") Integer sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return getRevisionUsecase.getRevisionOrderByUpdated(types, PagingInfo.of(sortBy, sortDirection, page, size));
    }

    @GetMapping("/documents/{documentId}/revisions")
    public Flux<RevisionHistory> getRevisionByDocument(
            @PathVariable String documentId,
            @RequestParam(defaultValue = "createdDateTime") String sortBy,
            @RequestParam(defaultValue = "1") Integer sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return getRevisionUsecase.getAllRevisionByDocument(documentId, PagingInfo.of(sortBy, sortDirection, page, size));
    }

    @GetMapping("/users/{userId}/revisions")
    public Flux<GetRevisionByUserResponse> getRevisionByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "createdDateTime") String sortBy,
            @RequestParam(defaultValue = "1") Integer sortDirection,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return getRevisionUsecase.getAllRevisionByUser(userId, PagingInfo.of(sortBy, sortDirection, page, size));
    }

}
