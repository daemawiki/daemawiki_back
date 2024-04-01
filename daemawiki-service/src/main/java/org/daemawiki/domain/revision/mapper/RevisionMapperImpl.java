package org.daemawiki.domain.revision.mapper;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.mapper.DocumentMapperImpl;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionMapperImpl implements RevisionMapper {
    private final GetDocumentPort getDocumentPort;
    private final DocumentMapperImpl documentMapper;

    public RevisionMapperImpl(GetDocumentPort getDocumentPort, DocumentMapperImpl documentMapper) {
        this.getDocumentPort = getDocumentPort;
        this.documentMapper = documentMapper;
    }

    @Override
    public Mono<SimpleDocumentResponse> revisionToRevisionSimpleDocumentResponse(RevisionHistory revisionHistory) {
        return getDocumentPort.getDocumentById(revisionHistory.getDocumentId())
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

}
