package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.model.Contents;
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
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContent {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;

    public WriteContent(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(WriteContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                    if (!Objects.equals(document.getVersion(), request.version())) {
                        throw VersionMismatchException.EXCEPTION;
                    }
                    return Tuples.of(user, document);
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    if (document.getEditor().hasEditPermission(user.getEmail())) {
                        return Mono.error(NoEditPermissionUserException.EXCEPTION);
                    }

                    Map<String, Contents> contentsMap = document.getContent().stream()
                            .collect(Collectors.toMap(Contents::getIndex, Function.identity()));

                    if (contentsMap.containsKey(request.index())) {
                        Contents content = contentsMap.get(request.index());
                        content.setContent(request.content());
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
