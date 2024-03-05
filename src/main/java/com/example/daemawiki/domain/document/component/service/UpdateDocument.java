package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.common.Commons;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
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
    private final Commons commons;

    public UpdateDocument(DocumentFacade documentFacade, UserFacade userFacade, GetDocumentType getDocumentType, RevisionComponent revisionComponent, Commons commons) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.getDocumentType = getDocumentType;
        this.revisionComponent = revisionComponent;
        this.commons = commons;
    }

    public Mono<Void> execute(SaveDocumentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                            commons.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());

                            document.getEditor().setUpdatedUser(UserDetailResponse.builder()
                                    .id(user.getId())
                                    .name(user.getName())
                                    .profile(user.getProfile())
                                    .build());

                            document.update(request.title(),
                                    getDocumentType.execute(request.type().toLowerCase()),
                                    request.groups());

                            document.getContents().add(request.content());
                            document.increaseVersion();

                            return document;
                        })
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                        .type(RevisionType.UPDATE)
                                        .documentId(document.getId())
                                        .title(document.getTitle())
                                        .build())))
                .onErrorMap(e -> e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}
