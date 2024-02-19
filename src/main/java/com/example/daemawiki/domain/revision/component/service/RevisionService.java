package com.example.daemawiki.domain.revision.component.service;

import com.example.daemawiki.domain.revision.dto.response.RevisionDocumentDetailResponse;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.mapper.RevisionMapper;
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
    private final RevisionMapper revisionMapper;
    private final Scheduler scheduler;

    public RevisionService(RevisionHistoryRepository revisionHistoryRepository, RevisionMapper revisionMapper, Scheduler scheduler) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.revisionMapper = revisionMapper;
        this.scheduler = scheduler;
    }

    public Flux<RevisionDocumentDetailResponse> getUpdatedTop10Revision() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);
        return revisionHistoryRepository.findTop10ByTypeInOrderByCreatedDateTimeDesc(types)
                .distinct(RevisionHistory::getDocumentId)
                .flatMap(revisionMapper::revisionToRevisionDocumentDetailResponse);
    }

    //        17077 92123
    //        17077 44486 최신
    public Flux<RevisionHistory> getFilteredRevisions(Flux<RevisionHistory> revisions, String lastRevisionId) {
        return revisions.filter(revisionHistory -> lastRevisionId.isBlank() ||
                        new ObjectId(revisionHistory.getId()).getTimestamp() > new ObjectId(lastRevisionId).getTimestamp())
                .take(20);
    }

    public Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByOrderByCreatedDateTimeDesc(),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByDocumentId(documentId),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

    public Flux<RevisionHistory> getAllRevisionByUser(String userId, String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByEditorOrderByCreatedDateTimeDesc(userId),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

}
