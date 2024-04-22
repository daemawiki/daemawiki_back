package org.daemawiki.domain.document.service;

import org.daemawiki.domain.document.application.DeleteDocumentPort;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.usecase.DeleteDocumentUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoEditPermissionUserException;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h403.StudentDocumentDeleteFailedException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class DeleteDocumentService implements DeleteDocumentUsecase {
    private final FindDocumentPort findDocumentPort;
    private final DeleteDocumentPort deleteDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final FindUserPort findUserPort;

    public DeleteDocumentService(FindDocumentPort findDocumentPort, DeleteDocumentPort deleteDocumentPort, CreateRevisionUsecase createRevisionUsecase, FindUserPort findUserPort) {
        this.findDocumentPort = findDocumentPort;
        this.deleteDocumentPort = deleteDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.findUserPort = findUserPort;
    }

    @Override
    public Mono<Void> delete(String documentId) {
        return findUserPort.currentUser()
                .filter(user -> !user.getIsBlocked())
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoEditPermissionUserException.EXCEPTION)))
                .zipWith(findDocumentPort.getDocumentById(documentId))
                .flatMap(this::getDefaultDocumentMono)
                .flatMap(this::deleteDocument)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> deleteDocument(DefaultDocument document) {
        return deleteDocumentPort.delete(document)
                .then(createRevision(document));
    }

    private Mono<DefaultDocument> getDefaultDocumentMono(Tuple2<User, DefaultDocument> tuple) {
        if (tuple.getT2().getType() == DocumentType.STUDENT) {
            return Mono.error(StudentDocumentDeleteFailedException.EXCEPTION);
        }
        if (!tuple.getT2().getEditor().getCreatedUser().id().equals(tuple.getT1().getId())) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }

        return Mono.just(tuple.getT2());
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return createRevisionUsecase.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.DELETE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof StudentDocumentDeleteFailedException || e instanceof NoPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
