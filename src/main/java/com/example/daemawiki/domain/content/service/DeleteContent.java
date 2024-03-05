package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.Commons;
import com.example.daemawiki.domain.content.dto.DeleteContentRequest;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteContent {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final Commons commons;

    public DeleteContent(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, Commons commons) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.commons = commons;
    }

    public Mono<Void> execute(DeleteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                    commons.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());
                    return document;
                })
                .flatMap(document -> {
                    document.getContent().removeIf(c -> c.getIndex().equals(request.index()));
                    return documentFacade.saveDocument(document)
                            .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                    .type(RevisionType.UPDATE)
                                    .documentId(documentId)
                                    .title(document.getTitle())
                                    .build()));
                })
                .onErrorMap(e -> e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}