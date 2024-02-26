package com.example.daemawiki.domain.info.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.info.dto.UpdateInfoRequest;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.domain.user.service.facade.UserFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdateInfo {
    private final DocumentFacade documentFacade;
    private final UserFacade userFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public UpdateInfo(DocumentFacade documentFacade, UserFacade userFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.userFacade = userFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(UpdateInfoRequest request) {
        return userFacade.currentUser()
                .flatMap(user -> documentFacade.findDocumentById(request.documentId())
                        .flatMap(document -> {
                            document.setInfo(request.infoList());
                            return documentRepository.save(document);
                        })
                        .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                                        .type(RevisionType.UPDATE)
                                        .documentId(request.documentId())
                                        .title(document.getTitle())
                                .build())));
    }

}
