package org.daemawiki.domain.revision.mapper;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionMapperImpl implements RevisionMapper {
    private final GetDocumentPort getDocumentPort;

    public RevisionMapperImpl(GetDocumentPort getDocumentPort) {
        this.getDocumentPort = getDocumentPort;
    }

    @Override
    public Mono<SimpleDocumentResponse> revisionToRevisionSimpleDocumentResponse(RevisionHistory revisionHistory) {
        return getDocumentPort.getDocumentById(revisionHistory.getDocumentId())
                .map(SimpleDocumentResponse::of);
    }

}
