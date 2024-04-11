package org.daemawiki.domain.revision.service;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.revision.mapper.RevisionMapper;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.persistence.RevisionPersistenceAdapter;
import org.daemawiki.domain.revision.usecase.GetRevisionUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GetRevisionService implements GetRevisionUsecase {
    private final RevisionPersistenceAdapter revisionPersistenceAdapter;
    private final RevisionMapper revisionMapper;

    public GetRevisionService(RevisionPersistenceAdapter revisionPersistenceAdapter, RevisionMapper revisionMapper) {
        this.revisionPersistenceAdapter = revisionPersistenceAdapter;
        this.revisionMapper = revisionMapper;
    }

    @Override
    public Flux<SimpleDocumentResponse> getUpdatedTop10Revision() {
        return revisionPersistenceAdapter.getUpdatedTop10Revision()
                .flatMap(revisionMapper::revisionToRevisionSimpleDocumentResponse);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionPaging(String lastRevisionId) {
        return revisionPersistenceAdapter.getAllRevisionPaging(lastRevisionId);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, String lastRevisionId) {
        return revisionPersistenceAdapter.getAllRevisionByDocument(documentId, lastRevisionId);
    }

    @Override
    public Flux<GetRevisionByUserResponse> getAllRevisionByUser(String userId, String lastRevisionId) {
        return revisionPersistenceAdapter.getAllRevisionByUser(userId, lastRevisionId)
                .map(GetRevisionByUserResponse::of);
    }

}
