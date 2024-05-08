package org.daemawiki.domain.document.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.component.UpdateDocumentComponent;
import org.daemawiki.domain.document.usecase.UpdateDocumentUsecase;
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
public class UpdateDocumentService implements UpdateDocumentUsecase {
    private final SaveDocumentPort saveDocumentPort;
    private final FindDocumentPort findDocumentPort;
    private final FindUserPort findUserPort;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final UserFilter userFilter;
    private final CreateRevisionComponent createRevisionComponent;

    public UpdateDocumentService(SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort, FindUserPort findUserPort, UpdateDocumentComponent updateDocumentComponent, UserFilter userFilter, CreateRevisionComponent createRevisionComponent) {
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
        this.findUserPort = findUserPort;
        this.updateDocumentComponent = updateDocumentComponent;
        this.userFilter = userFilter;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> update(SaveDocumentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.findById(documentId))
                .map(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .flatMap(this::saveDocumentAndCreateRevision)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> saveDocumentAndCreateRevision(DefaultDocument document) {
        return saveDocumentPort.save(document)
                .then(createRevisionComponent.create(document, RevisionType.UPDATE));
    }

    private DefaultDocument checkPermissionAndUpdateDocument(Tuple2<User, DefaultDocument> tuple, SaveDocumentRequest request) {
        DefaultDocument document = tuple.getT2();
        User user = tuple.getT1();

        userFilter.userPermissionAndDocumentVersionCheck(document, user, request.version());

        document.update(request.title(),
                DocumentType.valueOf(request.type().toUpperCase()),
                request.groups());

        document.getContents().add(request.content());
        document.increaseVersion();
        updateDocumentComponent.updateEditorAndUpdatedDate(document, user);

        return document;
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
