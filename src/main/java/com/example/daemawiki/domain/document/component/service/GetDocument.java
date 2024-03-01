package com.example.daemawiki.domain.document.component.service;

import com.example.daemawiki.domain.document.component.facade.DocumentFacade;
import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.DocumentSearchResult;
import com.example.daemawiki.domain.document.model.mapper.DocumentMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
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
                .doOnSuccess(DefaultDocument::increaseView)
                .flatMap(documentFacade::saveDocument)
                .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return documentFacade.findDocumentByRandom()
                .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

    public Flux<DocumentSearchResult> searchDocument(String text) {
        return documentFacade.searchDocument(text);
    }

    public Flux<SimpleDocumentResponse> getDocumentTop10() {
        return documentFacade.getDocumentOrderByVersion()
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

    public Flux<SimpleDocumentResponse> getDocumentOrderByView() {
        return documentFacade.getDocumentOrderByView()
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

}
