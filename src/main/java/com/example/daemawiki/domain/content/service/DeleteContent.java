package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.DeleteContentRequest;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class DeleteContentTable {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;

    public DeleteContentTable(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(DeleteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                    if (!Objects.equals(document.getVersion(), request.version())) {
                        throw VersionMismatchException.EXCEPTION;
                    }
                    if (document.getEditor().hasEditPermission(user.getEmail())) {
                        throw NoEditPermissionUserException.EXCEPTION;
                    }
                    return document;
                })
                .flatMap(document -> {
                    document.getContent().removeIf(c -> c.getIndex().equals(request.index()));
                    return documentFacade.saveDocument(document)
                            .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                    .type(RevisionType.DELETE)
                                    .documentId(documentId)
                                    .title(document.getTitle())
                                    .build()));
                });
    }

}
