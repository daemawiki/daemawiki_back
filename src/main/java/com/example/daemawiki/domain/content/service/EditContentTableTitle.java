package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import com.example.daemawiki.domain.content.model.Content;
import com.example.daemawiki.domain.document.component.UpdateDocumentEditorAndUpdatedDate;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
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
    private final UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate;

    public EditContentTableTitle(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, Scheduler scheduler, UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.scheduler = scheduler;
        this.updateDocumentEditorAndUpdatedDate = updateDocumentEditorAndUpdatedDate;
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
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());

        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        List<Content> contents = document.getContents()
                .stream()
                .filter(c -> c.getIndex().equals(request.index()))
                .toList();

        contents.getFirst().setTitle(request.newTitle());

        document.increaseVersion();
        updateDocumentEditorAndUpdatedDate.execute(document, user);

        return Mono.just(document);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

}
