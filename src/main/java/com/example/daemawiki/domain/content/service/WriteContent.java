package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.WriteContentRequest;
import com.example.daemawiki.domain.content.model.Contents;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h404.ContentNotFoundException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WriteContent {
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public WriteContent(DocumentFacade documentFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(WriteContentRequest request) {
        return documentFacade.findDocumentById(request.documentId())
                .flatMap(document -> {
                    Map<String, Contents> contentsMap = document.getContent().stream()
                            .collect(Collectors.toMap(Contents::getIndex, Function.identity()));

                    if (contentsMap.containsKey(request.index())) {
                        Contents content = contentsMap.get(request.index());
                        content.setContent(request.content());
                        return Mono.just(document);
                    } else {
                        return Mono.error(ContentNotFoundException.EXCEPTION);
                    }
                })
                .filter(document -> Objects.equals(document.getVersion(), request.version()))
                .switchIfEmpty(Mono.error(VersionMismatchException.EXCEPTION))
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(request.documentId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> e instanceof ContentNotFoundException || e instanceof VersionMismatchException ? e : ExecuteFailedException.EXCEPTION);
    }

}
