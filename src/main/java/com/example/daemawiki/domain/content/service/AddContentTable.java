package com.example.daemawiki.domain.content.service;

import com.example.daemawiki.domain.content.dto.AddContentRequest;
import com.example.daemawiki.domain.content.model.Contents;
import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.domain.revision.component.RevisionComponent;
import com.example.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import com.example.daemawiki.global.exception.h400.VersionMismatchException;
import com.example.daemawiki.global.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;

@Service
public class AddContentTable {
    private final DocumentFacade documentFacade;
    private final DocumentRepository documentRepository;
    private final RevisionComponent revisionComponent;

    public AddContentTable(DocumentFacade documentFacade, DocumentRepository documentRepository, RevisionComponent revisionComponent) {
        this.documentFacade = documentFacade;
        this.documentRepository = documentRepository;
        this.revisionComponent = revisionComponent;
    }

    public Mono<Void> execute(AddContentRequest request) {
        return documentFacade.findDocumentById(request.documentId())
                .map(document -> {
                    Contents newContent = Contents.builder()
                            .index(request.index())
                            .title(request.title())
                            .content("빈 내용")
                            .build();

                    document.getContent().add(newContent);

                    Comparator<Contents> customComparator = (c1, c2) -> {
                        String[] index1 = c1.getIndex().split("\\.");
                        String[] index2 = c2.getIndex().split("\\.");

                        for (int i = 0; i < Math.max(index1.length, index2.length); i++) {
                            int part1 = i < index1.length ? Integer.parseInt(index1[i]) : 0;
                            int part2 = i < index2.length ? Integer.parseInt(index2[i]) : 0;

                            if (part1 != part2) {
                                return part1 - part2;
                            }
                        }

                        return 0;
                    };

                    document.getContent().sort(customComparator);

                    return document;
                })
                .filter(document -> Objects.equals(document.getVersion(), request.version()))
                .switchIfEmpty(Mono.error(VersionMismatchException.EXCEPTION))
                .flatMap(documentRepository::save)
                .flatMap(document -> revisionComponent.saveHistory(SaveRevisionHistoryRequest.builder()
                        .type(RevisionType.UPDATE)
                        .documentId(request.documentId())
                        .title(document.getTitle())
                        .build()))
                .onErrorMap(e -> e instanceof VersionMismatchException ? e : ExecuteFailedException.EXCEPTION);
    }

}