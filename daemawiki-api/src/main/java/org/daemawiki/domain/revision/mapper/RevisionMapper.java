package org.daemawiki.domain.revision.mapper;

import org.daemawiki.domain.auth.mapper.DocumentMapper;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.revision.model.RevisionHistory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionMapper {
    private final DocumentFacade documentFacade;
    private final DocumentMapper documentMapper;

    public RevisionMapper(DocumentFacade documentFacade, DocumentMapper documentMapper) {
        this.documentFacade = documentFacade;
        this.documentMapper = documentMapper;
    }

    public Mono<SimpleDocumentResponse> revisionToRevisionSimpleDocumentResponse(RevisionHistory revisionHistory) {
        return documentFacade.findDocumentById(revisionHistory.getDocumentId())
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

}
