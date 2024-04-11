package org.daemawiki.domain.revision.persistence;

import org.bson.types.ObjectId;
import org.daemawiki.domain.revision.application.SaveRevisionPort;
import org.daemawiki.domain.revision.application.FindRevisionPort;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.repository.RevisionHistoryRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RevisionPersistenceAdapter implements SaveRevisionPort, FindRevisionPort {
    private final RevisionHistoryRepository revisionHistoryRepository;

    public RevisionPersistenceAdapter(RevisionHistoryRepository revisionHistoryRepository) {
        this.revisionHistoryRepository = revisionHistoryRepository;
    }

    @Override
    public Mono<Void> save(RevisionHistory revisionHistory) {
        return revisionHistoryRepository.save(revisionHistory)
                .then();
    }

    @Override
    public Flux<RevisionHistory> getUpdatedTop10Revision() {
        List<RevisionType> types = List.of(RevisionType.UPDATE, RevisionType.CREATE);

        return revisionHistoryRepository.findAllByTypeInOrderByCreatedDateTimeDesc(types)
                .distinct(RevisionHistory::getDocumentId)
                .take(10);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByOrderByCreatedDateTimeDesc(),
                lastRevisionId
        );
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByDocumentId(documentId),
                lastRevisionId
        );
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByUser(String userId, String lastRevisionId) {
        return getFilteredRevisions(
                revisionHistoryRepository.findAllByEditor_IdOrderByCreatedDateTimeDesc(userId),
                lastRevisionId
        );
    }

    private Flux<RevisionHistory> getFilteredRevisions(Flux<RevisionHistory> revisions, String lastRevisionId) {
        return revisions.filter(revisionHistory -> lastRevisionId.isBlank() ||
                        new ObjectId(revisionHistory.getId()).getTimestamp() > new ObjectId(lastRevisionId).getTimestamp())
                .take(20);
    }

}
