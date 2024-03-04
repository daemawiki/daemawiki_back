package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateDocument {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final RevisionComponent revisionComponent;
    private final CreateDocumentFacade createDocumentFacade;

    public CreateDocument(DocumentFacade documentFacade, UserFacade userFacade, RevisionComponent revisionComponent, CreateDocumentFacade createDocumentFacade) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.revisionComponent = revisionComponent;
        this.createDocumentFacade = createDocumentFacade;
    }

    public Mono<Void> execute(SaveDocumentRequest request) {
        return userFacade.currentUser()
                .map(user -> createDocumentFacade.execute(request, user))
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                        .type(RevisionType.CREATE)
                                        .documentId(document.getId())
                                        .title(document.getTitle())
                                        .build())))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

}
