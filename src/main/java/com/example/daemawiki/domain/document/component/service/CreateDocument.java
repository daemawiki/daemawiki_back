package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.document.component.facade.CreateDocumentFacade;
import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateDocument {
    private final DocumentRepository documentRepository;
    private final UserFacade userFacade;
    private final RevisionComponent revisionComponent;
    private final CreateDocumentFacade createDocumentFacade;

    public CreateDocument(DocumentRepository documentRepository, UserFacade userFacade, RevisionComponent revisionComponent, CreateDocumentFacade createDocumentFacade) {
        this.documentRepository = documentRepository;
        this.userFacade = userFacade;
        this.revisionComponent = revisionComponent;
        this.createDocumentFacade = createDocumentFacade;
    }

    public Mono<Void> execute(SaveDocumentRequest request) {
        return userFacade.currentUser()
                .map(user -> createDocumentFacade.execute(request, user))
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.CREATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build()));
    }

}