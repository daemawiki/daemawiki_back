package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h403.NoPermissionUserException;
import com.example.daemawiki.global.exception.h403.StudentDocumentDeleteFailedException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
                .flatMap(tuple -> {
                    if (tuple.getT2().getType() == DocumentType.STUDENT) {
                        return Mono.error(StudentDocumentDeleteFailedException.EXCEPTION);
                    }
                    if (!Objects.equals(tuple.getT2().getEditor().getCreatedUser().id(), tuple.getT1().getId())) {
                        return Mono.error(NoPermissionUserException.EXCEPTION);
                    }
                    return Mono.just(tuple.getT2());
                })
                .flatMap(document -> documentRepository.delete(document)
                        .then(createRevision(document)))
                .onErrorMap(this::mapException);
    }

    private Mono<Void> createRevision(DefaultDocument document) {
        return revisionComponent.saveHistory(SaveRevisionHistoryRequest
                .create(RevisionType.DELETE, document.getId(), document.getTitle()));
    }

    private Throwable mapException(Throwable e) {
        return e instanceof StudentDocumentDeleteFailedException || e instanceof NoPermissionUserException ? e : ExecuteFailedException.EXCEPTION;
    }

}
