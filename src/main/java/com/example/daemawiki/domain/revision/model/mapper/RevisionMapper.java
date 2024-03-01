package com.example.daemawiki.domain.revision.model.mapper;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.document.model.mapper.DocumentMapper;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
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
