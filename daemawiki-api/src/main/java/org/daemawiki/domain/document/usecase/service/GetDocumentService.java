package org.daemawiki.domain.document.usecase.service;

import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.mapper.DocumentMapper;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.usecase.GetDocumentUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetDocumentService implements GetDocumentUsecase {
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;
    private final DocumentMapper documentMapper;

    public GetDocumentService(GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort, DocumentMapper documentMapper) {
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
        this.documentMapper = documentMapper;
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentById(String id) {
        return getDocumentPort.getDocumentById(id)
                .doOnSuccess(DefaultDocument::increaseView)
                .flatMap(saveDocumentPort::save)
                .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return getDocumentPort.getDocumentByRandom()
                .flatMap(documentMapper::defaultDocumentToGetResponse);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocument(String text) {
        return getDocumentPort.searchDocument(text);
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentTop10() {
        return getDocumentPort.getDocumentTop10()
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentOrderByView() {
        return getDocumentPort.getDocumentOrderByView()
                .flatMap(documentMapper::defaultDocumentToSimpleDocumentResponse);
    }

}
