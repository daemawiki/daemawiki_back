package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.global.exception.h403.StudentDocumentDeleteFailedException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeleteDocument {
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public DeleteDocument(DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(String documentId) {
        return documentRepository.findById(documentId)
                .filter(document -> document.getType() != DocumentType.STUDENT)
                .switchIfEmpty(Mono.error(StudentDocumentDeleteFailedException.EXCEPTION))
                .flatMap(document -> documentRepository.delete(document)
                            .then(revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                            .type(RevisionType.DELETE)
                                            .documentId(documentId)
                                            .title(document.getTitle())
                                    .build()))
                        .onErrorMap(e -> ExecuteFailedException.EXCEPTION));
    }

}
