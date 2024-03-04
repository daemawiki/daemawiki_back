package com.example.daemawiki.domain.document.component.service;

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
                .zipWith(documentRepository.findById(documentId), (user, document) -> {
                    if (document.getType() == DocumentType.STUDENT) {
                        throw StudentDocumentDeleteFailedException.EXCEPTION;
                    }
                    if (!Objects.equals(document.getEditor().getCreatedUser().id(), user.getId())) {
                        throw NoPermissionUserException.EXCEPTION;
                    }
                    return document;
                })
                .flatMap(document -> documentRepository.delete(document)
                        .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                .type(RevisionType.DELETE)
                                .documentId(documentId)
                                .title(document.getTitle())
                                .build())))
                .onErrorMap(e -> e instanceof StudentDocumentDeleteFailedException || e instanceof NoPermissionUserException ? e : ExecuteFailedException.EXCEPTION);
    }

}
