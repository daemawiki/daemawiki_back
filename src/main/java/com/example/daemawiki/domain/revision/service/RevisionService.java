package com.example.daemawiki.domain.revision.service;

import com.example.daemawiki.domain.revision.dto.GetRevisionPageRequest;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@Service
public class RevisionService {
    private final RevisionHistoryRepository revisionHistoryRepository;
    private final Scheduler scheduler;

    public RevisionService(RevisionHistoryRepository revisionHistoryRepository, Scheduler scheduler) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.scheduler = scheduler;
    }

    public Flux<RevisionHistory> getUpdatedTop10Revision() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);
        return revisionHistoryRepository.findTop10ByTypeInOrderByCreatedDateTimeDesc(types)
                .distinct(RevisionHistory::getDocumentId);
    }

    //        17077 92123
    //        17077 44486 최신
    public Flux<RevisionHistory> getFilteredRevisions(Flux<RevisionHistory> revisions, String lastRevisionId) {
        return revisions.filter(revisionHistory -> lastRevisionId == null || lastRevisionId.isEmpty() ||
                        new ObjectId(revisionHistory.getId()).getTimestamp() > new ObjectId(lastRevisionId).getTimestamp())
                .take(20);
    }

    public Flux<RevisionHistory> getAllRevisionPaging(GetRevisionPageRequest request) {
        String lastRevisionId = request.lastRevisionId();
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByOrderByCreatedDateTimeDesc(),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, GetRevisionPageRequest request) {
        String lastRevisionId = request.lastRevisionId();
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByDocumentId(documentId),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

}
