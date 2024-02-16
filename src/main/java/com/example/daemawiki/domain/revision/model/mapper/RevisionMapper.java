package com.example.daemawiki.domain.revision.model.mapper;

import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.dto.response.RevisionDocumentDetailResponse;
import com.example.daemawiki.domain.revision.model.RevisionHistory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class RevisionMapper {
    private final DocumentFacade documentFacade;

    public RevisionMapper(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    public Mono<RevisionDocumentDetailResponse> revisionToRevisionDocumentDetailResponse(RevisionHistory revisionHistory) {
        return documentFacade.findDocumentById(revisionHistory.getDocumentId())
                .map(document -> RevisionDocumentDetailResponse.builder()
                        .documentId(revisionHistory.getDocumentId())
                        .title(revisionHistory.getTitle())
                        .numberOfRevision(document.getVersion())
                        .updatedDate(revisionHistory.getCreatedDateTime())
                        .build());
    }

}
