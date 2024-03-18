package org.daemawiki.domain.content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.revision.component.RevisionComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h404.ContentNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;

import java.util.List;

@Service
public class EditContentTableTitle {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;
    private final Scheduler scheduler;
    private final UpdateDocumentComponent updateDocumentComponent;

    public EditContentTableTitle(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, Scheduler scheduler, UpdateDocumentComponent updateDocumentComponent) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    public Mono<Void> execute(EditContentTableTitleRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .flatMap(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .subscribeOn(scheduler)
                .flatMap(document -> documentFacade.saveDocument(document)
                        .then(createRevision(document)));
    }

    private Mono<DefaultDocument> checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, EditContentTableTitleRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());

        List<Content> contents = document.getContents()
                .stream()
                .filter(c -> c.getIndex().equals(request.index()))
                .toList();

        if (contents.getFirst() != null){
            contents.getFirst().setTitle(request.newTitle());
        } else {
            return Mono.error(ContentNotFoundException.EXCEPTION);
        }

        document.increaseVersion();
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);

        return Mono.just(document);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
