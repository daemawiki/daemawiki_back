package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.DeleteContentRequest;
import com.example.daemawiki.domain.document.component.UpdateDocumentEditorAndUpdatedDate;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class DeleteContentTable {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;
    private final UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate;

    public DeleteContentTable(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.updateDocumentEditorAndUpdatedDate = updateDocumentEditorAndUpdatedDate;
    }

    public Mono<Void> execute(DeleteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> checkUserPermissionAndVersion(tuple, request.version()))
                .map(tuple -> deleteDocumentContentTable(tuple, request))
                .flatMap(document -> documentFacade.saveDocument(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument deleteDocumentContentTable(Tuple2<User, DefaultDocument> tuple, DeleteContentRequest request) {
        removeContent(tuple.getT2(), request.index());
        updateDocumentEditorAndUpdatedDate.execute(tuple.getT2(), tuple.getT1());

        return tuple.getT2();
    }

    private Tuple2<User, DefaultDocument> checkUserPermissionAndVersion(Tuple2<User, DefaultDocument> tuple, int version) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), version);
        return tuple;
    }


    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private void removeContent(DefaultDocument document, String index) {
        document.getContents().removeIf(c -> c.getIndex().equals(index));
        document.increaseVersion();
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
