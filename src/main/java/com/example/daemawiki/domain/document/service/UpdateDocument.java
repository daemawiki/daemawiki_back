package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.user.service.UserFacade;
import com.example.daemawiki.global.dateTime.facade.DateTimeFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedList;

@Service
public class UpdateDocument {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final DateTimeFacade dateTimeFacade;
    private final DocumentRepository documentRepository;
    private final GetDocumentType getDocumentType;
    private final RevisionComponent revisionComponent;

    public UpdateDocument(DocumentFacade documentFacade, UserFacade userFacade, DateTimeFacade dateTimeFacade, DocumentRepository documentRepository, GetDocumentType getDocumentType, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.dateTimeFacade = dateTimeFacade;
        this.documentRepository = documentRepository;
        this.getDocumentType = getDocumentType;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(SaveDocumentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> dateTimeFacade.getKor()
                        .flatMap(now -> {
                            LinkedList<String> groups = new LinkedList<>(request.groups());

                            document.update(request.title(),
                                    getDocumentType.execute(request.type()),
                                    request.content(),
                                    groups);

                            return documentRepository.save(document);
                        })
                        .flatMap(d -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                        .type(RevisionType.UPDATE)
                                        .documentId(d.getId())
                                        .title(d.getTitle())
                                .build())))
                .then();
    }

}
