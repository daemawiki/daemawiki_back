package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class EditContentTableTitle {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;
    private final Scheduler scheduler;

    public EditContentTableTitle(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, Scheduler scheduler) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
    }

    public Mono<Void> execute(EditContentTableTitleRequest request, String documentId) {
        return Mono.zip(userFacade.currentUser(), documentFacade.findDocumentById(documentId))
                .map(tuple -> userFilter.checkUserAndDocument(tuple.getT1(), tuple.getT2(), request.version()))
                .map(document -> updateDocument(document, request))
                .subscribeOn(scheduler)
                .flatMap(document -> documentFacade.saveDocument(document)
                        .then(createRevision(document)));
    }

    private DefaultDocument updateDocument(DefaultDocument document, EditContentTableTitleRequest request) {
        document.getContents()
                .stream()
                .filter(c -> c.getIndex().equals(request.index()))
                .findFirst()
                .ifPresent(contents -> contents.setTitle(request.newTitle()));

        document.increaseVersion();

        return document;
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                .type(RevisionType.UPDATE)
                .documentId(document.getId())
                .title(document.getTitle())
                .build());
    }

}
