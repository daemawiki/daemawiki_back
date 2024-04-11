package org.daemawiki.domain.document.service;

import org.daemawiki.domain.common.UserFilter;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.usecase.UpdateDocumentComponent;
import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.usecase.UpdateDocumentUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
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
    private final CreateRevisionUsecase createRevisionUsecase;
    private final UpdateDocumentComponent updateDocumentComponent;
    private final UserFilter userFilter;

    public UpdateDocumentService(SaveDocumentPort saveDocumentPort, FindDocumentPort findDocumentPort, FindUserPort findUserPort, CreateRevisionUsecase createRevisionUsecase, UpdateDocumentComponent updateDocumentComponent, UserFilter userFilter) {
        this.saveDocumentPort = saveDocumentPort;
        this.findDocumentPort = findDocumentPort;
        this.findUserPort = findUserPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.updateDocumentComponent = updateDocumentComponent;
        this.userFilter = userFilter;
    }

    @Override
    public Mono<Void> update(SaveDocumentRequest request, String documentId) {
        return findUserPort.currentUser()
                .zipWith(findDocumentPort.getDocumentById(documentId))
                .map(tuple -> checkPermissionAndUpdateDocument(tuple, request))
                .flatMap(document -> saveDocumentPort.save(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
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

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.UPDATE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof VersionMismatchException || e instanceof NoEditPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
