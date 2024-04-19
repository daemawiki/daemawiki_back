package org.daemawiki.domain.revision.usecase;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GetRevisionUsecase {
    Flux<SimpleDocumentResponse> getRevisionOrderByUpdated(List<String> types, PagingInfo pagingInfo);
    Flux<RevisionHistory> getAllRevisionPaging(PagingInfo pagingInfo);
    Flux<RevisionHistory> getAllRevisionByDocument(String documentId, PagingInfo pagingInfo);
    Flux<GetRevisionByUserResponse> getAllRevisionByUser(String userId, PagingInfo pagingInfo);

}
