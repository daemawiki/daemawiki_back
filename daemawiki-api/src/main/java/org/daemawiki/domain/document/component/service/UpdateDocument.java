package org.daemawiki.domain.document.component.service;

import org.daemawiki.domain.auth.type.GetDocumentType;
import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document.component.facade.DocumentFacade;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
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
public class UpdateDocument {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final GetDocumentType getDocumentType;
    private final RevisionComponent revisionComponent;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;

    public UpdateDocument(DocumentFacade documentFacade, UserFacade userFacade, GetDocumentType getDocumentType, RevisionComponent revisionComponent, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.getDocumentType = getDocumentType;
        this.revisionComponent = revisionComponent;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    public Mono<Void> execute(SaveDocumentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, SaveDocumentRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());

        document.update(request.title(),
                getDocumentType.execute(request.type().toLowerCase()),
                request.groups());

        document.getContents().add(request.content());
        document.increaseVersion();
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);

        return document;
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
