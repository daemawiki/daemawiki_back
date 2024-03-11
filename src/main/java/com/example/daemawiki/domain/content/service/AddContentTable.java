package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.common.UserFilter;
import com.example.daemawiki.domain.content.dto.AddContentRequest;
import com.example.daemawiki.domain.content.model.Content;
import com.example.daemawiki.domain.document.component.UpdateDocumentEditorAndUpdatedDate;
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

import java.util.Comparator;

@Service
public class AddContentTable {
    private final DocumentFacade documentFacade;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;
    private final UserFilter userFilter;
    private final UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate;


    public AddContentTable(DocumentFacade documentFacade, RevisionComponent revisionComponent, UserFacade userFacade, UserFilter userFilter, UpdateDocumentEditorAndUpdatedDate updateDocumentEditorAndUpdatedDate) {
        this.documentFacade = documentFacade;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
        this.userFilter = userFilter;
        this.updateDocumentEditorAndUpdatedDate = updateDocumentEditorAndUpdatedDate;
    }

    public Mono<Void> execute(AddContentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId))
                .map(tuple -> {
                    userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());
                    return tuple;
                })
                .flatMap(tuple -> {
                    DefaultDocument document = tuple.getT2();
                    User user = tuple.getT1();

                    updateDocumentEditorAndUpdatedDate.execute(document, user);
                    setDocumentContent(document, request.index(), request.title());
                    setDocument(document, user);

                    return Mono.just(document);
                })
                .flatMap(document -> documentFacade.saveDocument(document)
                                .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private void setDocumentContent(DefaultDocument document, String index, String title) {
        Content newContent = createContent(index, title);
        document.getContents().add(newContent);
    }

    private Content createContent(String index, String title) {
        return Content.create(index, title, "빈 내용");
    }

    private static final Comparator<Content> customComparator = (c1, c2) -> {
        if (c1 == null && c2 == null) {
            return 0;
        } else if (c1 == null) {
            return -1;
        } else if (c2 == null) {
            return 1;
        }

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

    private void sortContents(DefaultDocument document) {
        document.getContents().sort(customComparator);
    }

    private void setDocument(DefaultDocument document, User user) {
        document.getEditor().setUpdatedUser(UserDetailResponse.create(user));
        sortContents(document);
        document.increaseVersion();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
