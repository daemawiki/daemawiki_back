package com.example.daemawiki.domain.revision.component.service;

import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.mapper.RevisionMapper;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
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
    private final UserFacade userFacade;

    public RevisionService(RevisionHistoryRepository revisionHistoryRepository, RevisionMapper revisionMapper, Scheduler scheduler, UserFacade userFacade) {
        this.revisionHistoryRepository = revisionHistoryRepository;
        this.revisionMapper = revisionMapper;
        this.scheduler = scheduler;
        this.userFacade = userFacade;
    }

    public Flux<SimpleDocumentResponse> getUpdatedTop10Revision() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);
        return revisionHistoryRepository.findTop10ByTypeInOrderByCreatedDateTimeDesc(types)
                .distinct(RevisionHistory::getDocumentId)
                .flatMap(revisionMapper::revisionToRevisionSimpleDocumentResponse);
    }

    //        17077 92123
    //        17077 44486 최신
    private Flux<RevisionHistory> getFilteredRevisions(Flux<RevisionHistory> revisions, String lastRevisionId) {
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
                revisionHistoryRepository.findAllByEditor_IdOrderByCreatedDateTimeDesc(userId),
                lastRevisionId
        ).subscribeOn(scheduler);
    }

    public Flux<RevisionHistory> getAllRevisionByCurrentUser(String lastRevisionId) {
        return userFacade.currentUser()
                .map(user -> getAllRevisionByUser(user.getId(), lastRevisionId))
                .flatMap(Flux::collectList)
                .flatMapMany(Flux::fromIterable);
    }

}
