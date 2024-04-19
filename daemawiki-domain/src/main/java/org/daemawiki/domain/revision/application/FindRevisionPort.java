package org.daemawiki.domain.revision.application;

import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface FindRevisionPort {
    Flux<RevisionHistory> getRevisionOrderByUpdated(PagingInfo pagingInfo, List<String> types);
    Flux<RevisionHistory> getAllRevisionPaging(PagingInfo pagingInfo);
    Flux<RevisionHistory> getAllRevisionByDocument(String documentId, PagingInfo pagingInfo);
    Flux<RevisionHistory> getAllRevisionByUser(String userId, PagingInfo pagingInfo);
}
