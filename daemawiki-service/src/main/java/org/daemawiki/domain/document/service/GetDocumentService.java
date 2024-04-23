package org.daemawiki.domain.document.service;

import org.bson.types.ObjectId;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.GetMostViewDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.usecase.GetDocumentUsecase;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetDocumentService implements GetDocumentUsecase {
    private final FindDocumentPort findDocumentPort;
    private final SaveDocumentPort saveDocumentPort;

    public GetDocumentService(FindDocumentPort findDocumentPort, SaveDocumentPort saveDocumentPort) {
        this.findDocumentPort = findDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentById(String id) {
        return findDocumentPort.getDocumentById(id)
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return findDocumentPort.getDocumentByRandom()
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocument(String text, PagingInfo pagingInfo) {
        return findDocumentPort.searchDocument(text, pagingInfo);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentTitle(String text, PagingInfo pagingInfo) {
        return findDocumentPort.searchDocumentTitle(text, pagingInfo)
                .map(DocumentSearchResult::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentContent(String text, PagingInfo pagingInfo) {
        return findDocumentPort.searchDocumentContent(text, pagingInfo);
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentsMostRevision(PagingInfo pagingInfo) {
        return findDocumentPort.getDocumentMostRevision(pagingInfo).map(SimpleDocumentResponse::of);
    }

    @Override
    public Flux<GetMostViewDocumentResponse> getDocumentOrderByView(PagingInfo pagingInfo) {
        return findDocumentPort.getDocumentOrderByView(pagingInfo).map(GetMostViewDocumentResponse::of);
    }

}
