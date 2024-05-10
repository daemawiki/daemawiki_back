package org.daemawiki.domain.document_content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document_content.dto.AddContentRequest;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document_content.usecase.AddContentTableUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.dto.response.UserDetailVo;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.util.Comparator;

@Service
public class AddContentTableService implements AddContentTableUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final FindDocumentPort findDocumentPort;
    private final FindUserPort findUserPort;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final CreateRevisionComponent createRevisionComponent;

    public AddContentTableService(SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort, FindUserPort findUserPort, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent, CreateRevisionComponent createRevisionComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
        this.findUserPort = findUserPort;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> add(AddContentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .publishOn(Schedulers.boundedElastic())
                .map(tuple -> checkPermissionAndAddDocumentContentTable(tuple, request))
                .flatMap(this::saveDocumentAndCreateRevision)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE, null));
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
        return Content.of(index, title, "빈 내용");
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
        document.getContents()
                .sort(customComparator);
    }

    private void setDocument(DefaultDocument document, User user) {
        document.getEditor().updateUpdatedUser(UserDetailVo.create(user));
        sortContents(document);
        document.increaseVersion();
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
