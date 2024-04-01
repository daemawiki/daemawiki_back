package org.daemawiki.domain.content.usecase.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.content.dto.DeleteContentRequest;
import org.daemawiki.domain.content.usecase.RemoveContentTableUsecase;
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
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class RemoveContentTableService implements RemoveContentTableUsecase {
    private final GetDocumentPort getDocumentPort;
    private final GetUserPort getUserPort;
    private final SaveDocumentPort saveDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;

    public RemoveContentTableService(GetDocumentPort getDocumentPort, GetUserPort getUserPort, SaveDocumentPort saveDocumentPort, CreateRevisionUsecase createRevisionUsecase, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent) {
        this.getDocumentPort = getDocumentPort;
        this.getUserPort = getUserPort;
        this.saveDocumentPort = saveDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
    }

    @Override
    public Mono<Void> remove(DeleteContentRequest request, String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .map(tuple -> checkPermissionAndDeleteDocumentContentTable(tuple, request))
                .flatMap(document -> saveDocumentPort.save(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private DefaultDocument checkPermissionAndDeleteDocumentContentTable(Tuple2<User, DefaultDocument> tuple, DeleteContentRequest request) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1().getEmail(), request.version());

        removeContent(tuple.getT2(), request.index());
        updateDocumentComponent.updateEditorAndUpdatedDate(tuple.getT2(), tuple.getT1());

        return tuple.getT2();
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private void removeContent(DefaultDocument document, String index) {
        document.getContents().removeIf(content -> content.getIndex().equals(index));
        document.increaseVersion();
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
