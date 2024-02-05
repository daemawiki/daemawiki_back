package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.service.RevisionComponent;
import com.example.daemawiki.domain.user.service.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteDocument {
    private final DocumentRepository documentRepository;
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final RevisionComponent revisionComponent;

    public DeleteDocument(DocumentRepository documentRepository, DocumentFacade documentFacade, UserFacade userFacade, RevisionComponent revisionComponent) {
        this.documentRepository = documentRepository;
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(String documentId) {
        return documentRepository.findById(documentId)
                .flatMap(document -> documentRepository.delete(document)
                            .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                            .type(RevisionType.DELETE)
                                            .documentId(documentId)
                                            .title(document.getTitle())
                                    .build())));
    }

}
