package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.model.Content;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h404.ContentNotFoundException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContent {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;

    public WriteContent(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
    }

    public Mono<Void> execute(WriteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> {
                    userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());
                    return tuple;
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    Map<String, Content> contentsMap = document.getContents().stream()
                            .collect(Collectors.toMap(Content::getIndex, Function.identity()));

                    if (contentsMap.containsKey(request.index())) {
                        Content content = contentsMap.get(request.index());
                        content.setDetail(request.content());
                        setDocument(document, user);

                        return documentFacade.saveDocument(document)
                                .then(createRevision(document));
                    } else {
                        return Mono.error(ContentNotFoundException.EXCEPTION);
                    }
                })
                .onErrorMap(this::mapException);
    }

    private void setDocument(DefaultDocument document, User user) {
        document.getEditor().setUpdatedUser(UserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile(user.getProfile())
                .build());

        document.increaseVersion();
    }


    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                .type(RevisionType.UPDATE)
                .documentId(document.getId())
                .title(document.getTitle())
                .build());
    }

    private Throwable mapException(Throwable e) {
        return e instanceof ContentNotFoundException || e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
