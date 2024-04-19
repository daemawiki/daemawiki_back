package org.daemawiki.domain.revision.service;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.revision.mapper.RevisionMapper;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.persistence.RevisionPersistenceAdapter;
import org.daemawiki.domain.revision.usecase.GetRevisionUsecase;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class GetRevisionService implements GetRevisionUsecase {
    private final RevisionPersistenceAdapter revisionPersistenceAdapter;
    private final RevisionMapper revisionMapper;

    public GetRevisionService(RevisionPersistenceAdapter revisionPersistenceAdapter, RevisionMapper revisionMapper) {
        this.revisionPersistenceAdapter = revisionPersistenceAdapter;
        this.revisionMapper = revisionMapper;
    }

    @Override
    public Flux<SimpleDocumentResponse> getRevisionOrderByUpdated(List<String> types, PagingInfo pagingInfo) {
        return revisionPersistenceAdapter.getRevisionOrderByUpdated(pagingInfo, types)
                .flatMap(revisionMapper::revisionToRevisionSimpleDocumentResponse);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionPaging(PagingInfo pagingInfo) {
        return revisionPersistenceAdapter.getAllRevisionPaging(pagingInfo);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, PagingInfo pagingInfo) {
        return revisionPersistenceAdapter.getAllRevisionByDocument(documentId, pagingInfo);
    }

    @Override
    public Flux<GetRevisionByUserResponse> getAllRevisionByUser(String userId, PagingInfo pagingInfo) {
        return revisionPersistenceAdapter.getAllRevisionByUser(userId, pagingInfo)
                .map(GetRevisionByUserResponse::of);
    }

}
