package org.daemawiki.domain.document_revision.mapper;

import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionMapperImpl implements RevisionMapper {
    private final FindDocumentPort findDocumentPort;

    public RevisionMapperImpl(FindDocumentPort findDocumentPort) {
        this.findDocumentPort = findDocumentPort;
    }

    @Override
    public Mono<SimpleDocumentResponse> revisionToRevisionSimpleDocumentResponse(RevisionHistory revisionHistory) {
        return findDocumentPort.findById(revisionHistory.getDocumentId())
                .map(SimpleDocumentResponse::of);
    }

}
