package org.daemawiki.domain.revision.usecase;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import reactor.core.publisher.Flux;

public interface GetRevisionUsecase {
    Flux<SimpleDocumentResponse> getUpdatedTop10Revision();
    Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId);
    Flux<RevisionHistory> getAllRevisionByDocument(String documentId, String lastRevisionId);
    Flux<GetRevisionByUserResponse> getAllRevisionByUser(String userId, String lastRevisionId);

}
