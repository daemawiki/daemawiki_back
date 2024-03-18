package org.daemawiki.domain.document.component.service;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.domain.revision.component.RevisionComponent;
import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.service.facade.UserFacade;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.daemawiki.exception.h403.StudentDocumentDeleteFailedException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Objects;

@Service
public class DeleteDocument {
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;
    private final UserFacade userFacade;

    public DeleteDocument(DocumentRepository documentRepository, RevisionComponent revisionComponent, UserFacade userFacade) {
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
        this.userFacade = userFacade;
    }

    public Mono<Void> execute(String documentId) {
        return userFacade.currentUser()
                .zipWith(documentRepository.findById(documentId))
                .flatMap(this::getDefaultDocumentMono)
                .flatMap(document -> documentRepository.delete(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private Mono<DefaultDocument> getDefaultDocumentMono(Tuple2<User, DefaultDocument> tuple) {
        if (tuple.getT2().getType() == DocumentType.STUDENT) {
            return Mono.error(StudentDocumentDeleteFailedException.EXCEPTION);
        }
        if (!Objects.equals(tuple.getT2().getEditor().getCreatedUser().id(), tuple.getT1().getId())) {
            return Mono.error(NoPermissionUserException.EXCEPTION);
        }

        return Mono.just(tuple.getT2());
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.DELETE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof StudentDocumentDeleteFailedException || e instanceof NoPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
