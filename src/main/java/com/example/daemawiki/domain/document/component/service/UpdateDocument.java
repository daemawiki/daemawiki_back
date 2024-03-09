package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateDocument {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final GetDocumentType getDocumentType;
    private final RevisionComponent revisionComponent;
    private final UserFilter userFilter;

    public UpdateDocument(DocumentFacade documentFacade, UserFacade userFacade, GetDocumentType getDocumentType, RevisionComponent revisionComponent, UserFilter userFilter) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.getDocumentType = getDocumentType;
        this.revisionComponent = revisionComponent;
        this.userFilter = userFilter;
    }

    public Mono<Void> execute(SaveDocumentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> {
                    userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());
                    return tuple;
                })
                .map(tuple -> setDocument(tuple.getT2(), tuple.getT1(), request))
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument setDocument(DefaultDocument document, User user, SaveDocumentRequest request) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));

        document.update(request.title(),
                getDocumentType.execute(request.type().toLowerCase()),
                request.groups());

        document.getContents().add(request.content());
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

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
