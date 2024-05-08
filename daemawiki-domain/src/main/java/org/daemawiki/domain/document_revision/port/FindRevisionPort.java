package org.daemawiki.domain.document_revision.port;

import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface FindRevisionPort {
    Flux<RevisionHistory> findAllSortByUpdatedDate(PagingInfo pagingInfo, List<String> types);
    Flux<RevisionHistory> findAll(PagingInfo pagingInfo);
    Flux<RevisionHistory> findAllByDocumentId(String documentId, PagingInfo pagingInfo);
    Flux<RevisionHistory> findAllByUserId(String userId, PagingInfo pagingInfo);
}
