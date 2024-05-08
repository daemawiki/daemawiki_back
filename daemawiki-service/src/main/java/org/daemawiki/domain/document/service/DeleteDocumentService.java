package org.daemawiki.domain.document.service;

import org.daemawiki.domain.document.port.DeleteDocumentPort;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.usecase.DeleteDocumentUsecase;
import org.daemawiki.domain.document_revision.component.CreateRevisionComponent;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.daemawiki.domain.user.port.FindUserPort;
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
    private final FindUserPort findUserPort;
    private final CreateRevisionComponent createRevisionComponent;

    public DeleteDocumentService(FindDocumentPort findDocumentPort, DeleteDocumentPort deleteDocumentPort, FindUserPort findUserPort, CreateRevisionComponent createRevisionComponent) {
        this.findDocumentPort = findDocumentPort;
        this.deleteDocumentPort = deleteDocumentPort;
        this.findUserPort = findUserPort;
        this.createRevisionComponent = createRevisionComponent;
    }

    @Override
    public Mono<Void> delete(String documentId) {
        return findUserPort.currentUser()
                .filter(user -> !user.getIsBlocked())
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoEditPermissionUserException.EXCEPTION)))
                .zipWith(findDocumentPort.findById(documentId))
                .flatMap(this::getDefaultDocumentMono)
                .flatMap(this::deleteDocument)
                .onErrorMap(this::mapException);
    }

    private Mono<Void> deleteDocument(DefaultDocument document) {
        return deleteDocumentPort.delete(document)
                .then(createRevisionComponent.create(document, RevisionType.DELETE));
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

    private Throwable mapException(Throwable e) {
        return e instanceof StudentDocumentDeleteFailedException || e instanceof NoPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
