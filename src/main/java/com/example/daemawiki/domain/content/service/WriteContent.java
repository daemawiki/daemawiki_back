package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.model.Contents;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.dto.response.UserDetailResponse;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h403.NoEditPermissionUserException;
import com.example.daemawiki.global.exception.h404.ContentNotFoundException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContent {
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;

    public WriteContent(DocumentFacade documentFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent, UserFacade userFacade) {
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(WriteContentRequest request) {
        return userFacade.currentUser()
                .flatMap(user -> documentFacade.findDocumentById(request.documentId())
                        .filter(document -> Objects.equals(document.getVersion(), request.version()))
                        .switchIfEmpty(Mono.error(VersionMismatchException.EXCEPTION))
                        .flatMap(document -> {
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
                        .flatMap(documentRepository::save))
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(request.documentId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> e instanceof ContentNotFoundException || e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}
