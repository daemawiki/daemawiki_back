package org.daemawiki.domain.document_revision.service;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document_revision.port.FindRevisionPort;
import org.daemawiki.domain.document_revision.dto.response.GetRevisionByUserResponse;
import org.daemawiki.domain.document_revision.mapper.RevisionMapper;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.domain.document_revision.usecase.GetRevisionUsecase;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
public class GetRevisionService implements GetRevisionUsecase {
    private final FindRevisionPort findRevisionPort;
    private final RevisionMapper revisionMapper;

    public GetRevisionService(FindRevisionPort findRevisionPort, RevisionMapper revisionMapper) {
        this.findRevisionPort = findRevisionPort;
        this.revisionMapper = revisionMapper;
    }

    @Override
    public Flux<SimpleDocumentResponse> getRevisionOrderByUpdated(List<String> types, PagingInfo pagingInfo) {
        return findRevisionPort.findAllSortByUpdatedDate(pagingInfo, types)
                .flatMap(revisionMapper::revisionToRevisionSimpleDocumentResponse);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionPaging(PagingInfo pagingInfo) {
        return findRevisionPort.findAll(pagingInfo);
    }

    @Override
    public Flux<RevisionHistory> getAllRevisionByDocument(String documentId, PagingInfo pagingInfo) {
        return findRevisionPort.findAllByDocumentId(documentId, pagingInfo);
    }

    @Override
    public Flux<GetRevisionByUserResponse> getAllRevisionByUser(String userId, PagingInfo pagingInfo) {
        return findRevisionPort.findAllByUserId(userId, pagingInfo)
                .map(GetRevisionByUserResponse::of);
    }

}
