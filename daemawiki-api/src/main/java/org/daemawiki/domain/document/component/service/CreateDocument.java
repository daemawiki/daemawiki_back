package org.daemawiki.domain.document.component.service;

import org.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.revision.component.RevisionComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h500.ExecuteFailedException;
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
                .flatMap(user -> createDocumentFacade.execute(request, user))
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(createRevision(document)))
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.CREATE, document.getId(), document.getTitle()));
    }

}
