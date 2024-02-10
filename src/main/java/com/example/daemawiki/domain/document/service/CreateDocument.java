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
import com.example.daemawiki.global.dateTime.facade.DateTimeFacade;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateDocument {
    private final DocumentRepository documentRepository;
    private final UserFacade userFacade;
    private final DateTimeFacade dateTimeFacade;
    private final RevisionComponent revisionComponent;
    private final GetDocumentType getDocumentType;

    public CreateDocument(DocumentRepository documentRepository, UserFacade userFacade, DateTimeFacade dateTimeFacade, RevisionComponent revisionComponent, GetDocumentType getDocumentType) {
        this.documentRepository = documentRepository;
        this.userFacade = userFacade;
        this.dateTimeFacade = dateTimeFacade;
        this.revisionComponent = revisionComponent;
        this.getDocumentType = getDocumentType;
    }

    public Mono<Void> execute(SaveDocumentRequest request) {
        return userFacade.currentUser()
                .zipWith(dateTimeFacade.getKor(), (user, now) -> DefaultDocument.builder()
                            .title(request.title())
                            .type(getDocumentType.execute(request.type()))
                            .dateTime(EditDateTime.builder()
                                    .created(now)
                                    .updated(now)
                                    .build())
                            .documentEditor(DocumentEditor.builder()
                                    .createdUser(user)
                                    .updatedUser(user)
                                    .build())
                            .content(request.content())
                            .groups(Lists.mutable.with(String.join("/", request.groups())))
                            .build())
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.CREATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build()));
    }

}
