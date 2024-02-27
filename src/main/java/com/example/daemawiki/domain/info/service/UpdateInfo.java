package com.example.daemawiki.domain.info.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.info.dto.UpdateInfoRequest;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class UpdateInfo {
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public UpdateInfo(DocumentFacade documentFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(UpdateInfoRequest request) {
        return documentFacade.findDocumentById(request.documentId())
                .filter(document -> Objects.equals(document.getVersion(), request.version()))
                .switchIfEmpty(Mono.error(VersionMismatchException.EXCEPTION))
                .flatMap(document -> {
                    document.setInfo(request.infoList());
                    return documentRepository.save(document);
                })
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(request.documentId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> e instanceof VersionMismatchException ? e : ExecuteFailedException.EXCEPTION);
    }

}
