package org.daemawiki.domain.document_revision.mapper;

import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import reactor.core.publisher.Mono;

public interface RevisionMapper {
    Mono<SimpleDocumentResponse> revisionToRevisionSimpleDocumentResponse(RevisionHistory revisionHistory);

}
