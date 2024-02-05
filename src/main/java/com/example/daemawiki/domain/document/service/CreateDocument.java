package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.request.CreateDocumentRequest;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.DocumentEditor;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.service.RevisionComponent;
import com.example.daemawiki.domain.user.service.UserFacade;
import com.example.daemawiki.global.dateTime.facade.DateTimeFacade;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CreateDocument {
    private final DocumentRepository documentRepository;
    private final UserFacade userFacade;
    private final DateTimeFacade dateTimeFacade;
    private final RevisionComponent revisionComponent;

    public CreateDocument(DocumentRepository documentRepository, UserFacade userFacade, DateTimeFacade dateTimeFacade, RevisionComponent revisionComponent) {
        this.documentRepository = documentRepository;
        this.userFacade = userFacade;
        this.dateTimeFacade = dateTimeFacade;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(CreateDocumentRequest request) {
        return userFacade.currentUser()
                .zipWith(dateTimeFacade.getKor(), (user, now) -> DefaultDocument.builder()
                            .title(request.title())
                            .type(switch (request.type()) {
                                case "student" -> DocumentType.STUDENT;
                                case "teacher" -> DocumentType.TEACHER;
                                case "club" -> DocumentType.CLUB;
                                case "gen" -> DocumentType.GEN;
                                case "major" -> DocumentType.MAJOR;
                                case null, default -> DocumentType.DEFAULT;
                            })
                            .dateTime(EditDateTime.builder()
                                    .created(now)
                                    .updated(now)
                                    .build())
                            .documentEditor(DocumentEditor.builder()
                                    .createdUser(user)
                                    .updatedUser(user)
                                    .build())
                            .build())
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.CREATE)
                        .documentId(document.getId())
                        .title(document.getTitle())
                        .build()));
    }

}
