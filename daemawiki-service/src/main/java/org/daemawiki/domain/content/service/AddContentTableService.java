package org.daemawiki.domain.content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.AddContentRequest;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.content.usecase.AddContentTableUsecase;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.dto.response.UserDetailResponse;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Comparator;

@Service
public class AddContentTableService implements AddContentTableUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final FindDocumentPort findDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final FindUserPort findUserPort;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;

    public AddContentTableService(SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort, CreateRevisionUsecase createRevisionUsecase, FindUserPort findUserPort, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.findUserPort = findUserPort;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    @Override
    public Mono<Void> add(AddContentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.getDocumentById(documentId))
                .map(tuple -> checkPermissionAndAddDocumentContentTable(tuple, request))
                .flatMap(document -> saveDocumentPort.save(document)
                                .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument checkPermissionAndAddDocumentContentTable(Tuple2<User, DefaultDocument> tuple, AddContentRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user, request.version());

        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);
        setDocumentContent(document, request.index(), request.title());
        setDocument(document, user);

        return document;
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
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
