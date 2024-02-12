package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.DocumentEditor;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.user.service.UserFacade;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CreateDocument {
    private final DocumentRepository documentRepository;
    private final UserFacade userFacade;
    private final RevisionComponent revisionComponent;
    private final GetDocumentType getDocumentType;

    public CreateDocument(DocumentRepository documentRepository, UserFacade userFacade, RevisionComponent revisionComponent, GetDocumentType getDocumentType) {
        this.documentRepository = documentRepository;
        this.userFacade = userFacade;
        this.revisionComponent = revisionComponent;
        this.getDocumentType = getDocumentType;
    }

    public Mono<Void> execute(SaveDocumentRequest request) {
        return userFacade.currentUser()
                .map(user -> DefaultDocument.builder()
                        .title(request.title())
                        .type(getDocumentType.execute(request.type()))
                        .dateTime(EditDateTime.builder()
                                .created(LocalDateTime.now())
                                .updated(LocalDateTime.now())
                                .build())
                        .documentEditor(DocumentEditor.builder()
                                .createdUser(user)
                                .updatedUser(user)
                                .build())
                        .content(request.content())
                        .groups(request.groups().stream()
                                .filter(Objects::nonNull)
                                .map(group -> String.join("/", group))
                                .toList())
                        .build())
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.CREATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build()));
    }

}
