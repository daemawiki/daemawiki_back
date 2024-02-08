package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetDocument {
    private final DocumentFacade documentFacade;

    public GetDocument(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    public Mono<GetDocumentResponse> execute(String id) {
        return documentFacade.findDocumentById(id)
                .map(document -> GetDocumentResponse.builder()
                        .title(document.getTitle())
                        .type(document.getType())
                        .dateTime(document.getDateTime())
                        .groups(document.getGroups())
                        .editor(document.getEditor())
                        .content(document.getContent())
                        .build());
    }

}
