package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.domain.document.service.mapper.DocumentMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetDocument {
    private final DocumentFacade documentFacade;
    private final DocumentMapper documentMapper;

    public GetDocument(DocumentFacade documentFacade, DocumentMapper documentMapper) {
        this.documentFacade = documentFacade;
        this.documentMapper = documentMapper;
    }

    public Mono<GetDocumentResponse> getDocumentById(String id) {
        return documentFacade.findDocumentById(id)
                        .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return documentFacade.findDocumentByRandom()
                .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

}
