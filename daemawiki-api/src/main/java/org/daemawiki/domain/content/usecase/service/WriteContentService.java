package org.daemawiki.domain.content.usecase.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.WriteContentRequest;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.content.usecase.WriteContentUsecase;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h404.ContentNotFoundException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContentService implements WriteContentUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final GetDocumentPort getDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final GetUserPort getUserPort;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;

    public WriteContentService(SaveDocumentPort saveDocumentPort, GetDocumentPort getDocumentPort, CreateRevisionUsecase createRevisionUsecase, GetUserPort getUserPort, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.getDocumentPort = getDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.getUserPort = getUserPort;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    @Override
    public Mono<Void> write(WriteContentRequest request, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(tuple -> checkPermissionAndWriteContent(tuple, request))
                .flatMap(document -> saveDocumentPort.save(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private Mono<DefaultDocument> checkPermissionAndWriteContent(Tuple2<User, DefaultDocument> tuple, WriteContentRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user.getEmail(), request.version());

        Map<String, Content> contentsMap = document.getContents().stream()
                .collect(Collectors.toMap(Content::getIndex, Function.identity()));

        if (contentsMap.containsKey(request.index())) {
            Content content = contentsMap.get(request.index());
            content.setDetail(request.content());
            setDocument(document, user);

            return Mono.just(document);
        } else {
            return Mono.error(ContentNotFoundException.EXCEPTION);
        }
    }

    private void setDocument(DefaultDocument document, User user) {
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);
        document.increaseVersion();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return (e instanceof ContentNotFoundException || e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException) ? e : ExecuteFailedException.EXCEPTION;
    }
}
