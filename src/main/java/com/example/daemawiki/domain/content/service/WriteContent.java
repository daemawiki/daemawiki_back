package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.Commons;
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
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContent {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final Commons commons;

    public WriteContent(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, Commons commons) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.commons = commons;
    }

    public Mono<Void> execute(WriteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                    commons.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());
                    return Tuples.of(user, document);
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    Map<String, Content> contentsMap = document.getContents().stream()
                            .collect(Collectors.toMap(Content::getIndex, Function.identity()));

                    if (contentsMap.containsKey(request.index())) {
                        Content content = contentsMap.get(request.index());
                        content.setDetail(request.content());
                        document.getEditor().setUpdatedUser(UserDetailResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .profile(user.getProfile())
                                .build());
                        document.increaseVersion();
                        return Mono.just(document);
                    } else {
                        return Mono.error(ContentNotFoundException.EXCEPTION);
                    }
                })
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                        .type(RevisionType.UPDATE)
                                        .documentId(documentId)
                                        .title(document.getTitle())
                                        .build())))
                .onErrorMap(e -> e instanceof ContentNotFoundException || e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}
