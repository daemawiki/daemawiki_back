package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import com.example.daemawiki.domain.document.model.type.service.GetDocumentType;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
public class UpdateDocument {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final DocumentRepository documentRepository;
    private final GetDocumentType getDocumentType;
    private final RevisionComponent revisionComponent;

    public UpdateDocument(DocumentFacade documentFacade, UserFacade userFacade, DocumentRepository documentRepository, GetDocumentType getDocumentType, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.documentRepository = documentRepository;
        this.getDocumentType = getDocumentType;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(SaveDocumentRequest request, String documentId) {
        return userFacade.currentUser()
                .zipWith(documentFacade.findDocumentById(documentId), (user, document) -> {
                            List<String> groups = request.groups().stream()
                                    .filter(Objects::nonNull)
                                    .map(group -> String.join("/", group))
                                    .toList();

                            document.update(request.title(),
                                    getDocumentType.execute(request.type()),
                                    request.content(),
                                    groups);

                            return document;
                        })
                .filter(document -> Objects.equals(document.getVersion(), request.version()))
                .switchIfEmpty(Mono.error(VersionMismatchException.EXCEPTION))
                .flatMap(documentRepository::save)
                .flatMap(d -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                .type(RevisionType.UPDATE)
                                .documentId(d.getId())
                                .title(d.getTitle())
                                .build()));
    }

}
