package org.daemawiki.domain.document.usecase.service;

import org.daemawiki.domain.document.application.DeleteDocumentPort;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.usecase.DeleteDocumentUsecase;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.revision.usecase.CreateRevisionUsecase;
import org.daemawiki.domain.user.application.GetUserPort;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h403.StudentDocumentDeleteFailedException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Service
public class DeleteDocumentService implements DeleteDocumentUsecase {
    private final GetDocumentPort getDocumentPort;
    private final DeleteDocumentPort deleteDocumentPort;
    private final CreateRevisionUsecase createRevisionUsecase;
    private final GetUserPort getUserPort;

    public DeleteDocumentService(GetDocumentPort getDocumentPort, DeleteDocumentPort deleteDocumentPort, CreateRevisionUsecase createRevisionUsecase, GetUserPort getUserPort) {
        this.getDocumentPort = getDocumentPort;
        this.deleteDocumentPort = deleteDocumentPort;
        this.createRevisionUsecase = createRevisionUsecase;
        this.getUserPort = getUserPort;
    }

    @Override
    public Mono<Void> delete(String documentId) {
        return getUserPort.currentUser()
                .zipWith(getDocumentPort.getDocumentById(documentId))
                .flatMap(this::getDefaultDocumentMono)
                .flatMap(document -> deleteDocumentPort.delete(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
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
