package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.AddContentRequest;
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
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Comparator;
import java.util.Objects;

@Service
public class AddContentTable {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;

    public AddContentTable(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(AddContentRequest request) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(request.documentId()), (user, document) -> {
                    if (!Objects.equals(document.getVersion(), request.version())) {
                        throw VersionMismatchException.EXCEPTION;
                    }
                    if (document.getEditor().hasEditPermission(user.getEmail())) {
                        throw NoEditPermissionUserException.EXCEPTION;
                    }
                    return Tuples.of(document, user);
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT1();
                    User user = tuple.getT2();

                    Contents newContent = Contents.builder()
                            .index(request.index())
                            .title(request.title())
                            .content("빈 내용")
                            .build();

                    document.getContent().add(newContent);

                    Comparator<Contents> customComparator = (c1, c2) -> {
                        String[] index1 = c1.getIndex().split("\\.");
                        String[] index2 = c2.getIndex().split("\\.");

                        for (int i = 0; i < Math.max(index1.length, index2.length); i++) {
                            int part1 = i < index1.length ? Integer.parseInt(index1[i]) : 0;
                            int part2 = i < index2.length ? Integer.parseInt(index2[i]) : 0;

                            if (part1 != part2) {
                                return part1 - part2;
                            }
                        }

                        return 0;
                    };

                    document.getEditor().setUpdatedUser(UserDetailResponse.builder()
                            .id(user.getId())
                            .name(user.getName())
                            .profile(user.getProfile())
                            .build());

                    document.getContent().sort(customComparator);
                    document.increaseVersion();

                    return Mono.just(document);
                })
                .flatMap(documentFacade::saveDocument)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(request.documentId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}
