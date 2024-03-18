package org.daemawiki.domain.content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.DeleteContentRequest;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.revision.component.RevisionComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class DeleteContentTable {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;

    public DeleteContentTable(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    public Mono<Void> execute(DeleteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> checkPermissionAndDeleteDocumentContentTable(tuple, request))
                .flatMap(document -> documentFacade.saveDocument(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument checkPermissionAndDeleteDocumentContentTable(Tuple2<User, DefaultDocument> tuple, DeleteContentRequest request) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());

        removeContent(tuple.getT2(), request.index());
        updateDocumentComponent.updateEditorAndUpdatedDate(tuple.getT2(), tuple.getT1());

        return tuple.getT2();
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
