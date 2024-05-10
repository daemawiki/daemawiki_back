package org.daemawiki.domain.document_content.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document_content.dto.DeleteContentRequest;
import org.daemawiki.domain.document_content.usecase.RemoveContentTableUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h400.VersionMismatchException;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class RemoveContentTableService implements RemoveContentTableUsecase {
    private final FindDocumentPort findDocumentPort;
    private final FindUserPort findUserPort;
    private final SaveDocumentPort saveDocumentPort;
    private final UserFilter userFilter;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final CreateRevisionComponent createRevisionComponent;

    public RemoveContentTableService(FindDocumentPort findDocumentPort, FindUserPort findUserPort, SaveDocumentPort saveDocumentPort, UserFilter userFilter, UpdateDocumentComponent updateDocumentComponent, CreateRevisionComponent createRevisionComponent) {
        this.findDocumentPort = findDocumentPort;
        this.findUserPort = findUserPort;
        this.saveDocumentPort = saveDocumentPort;
        this.userFilter = userFilter;
        this.updateDocumentComponent = updateDocumentComponent;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> remove(DeleteContentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .map(tuple -> checkPermissionAndDeleteDocumentContentTable(tuple, request))
                .flatMap(this::saveDocumentAndCreateRevision)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE, null));
    }

    private DefaultDocument checkPermissionAndDeleteDocumentContentTable(Tuple2<User, DefaultDocument> tuple, DeleteContentRequest request) {
        userFilter.userPermissionAndDocumentVersionCheck(tuple.getT2(), tuple.getT1(), request.version());

        removeContent(tuple.getT2(), request.index());
        updateDocumentComponent.updateEditorAndUpdatedDate(tuple.getT2(), tuple.getT1());

        return tuple.getT2();
    }

    private void removeContent(DefaultDocument document, String index) {
        document.getContents().removeIf(content -> content.getIndex().equals(index));
        document.increaseVersion();
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
