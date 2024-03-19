package org.daemawiki.domain.revision.application;

import org.daemawiki.domain.revision.model.RevisionHistory;
import reactor.core.publisher.Flux;

public interface GetRevisionPort {
    Flux<RevisionHistory> getUpdatedTop10Revision();
    Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId);
    Flux<RevisionHistory> getAllRevisionByDocument(String documentId, String lastRevisionId);
    Flux<RevisionHistory> getAllRevisionByUser(String userId, String lastRevisionId);
}
