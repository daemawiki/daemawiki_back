package org.daemawiki.domain.document.service;

import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.GetMostViewDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.usecase.GetDocumentUsecase;
import org.daemawiki.exception.h404.DocumentNotFoundException;
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
        return findDocumentPort.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(DocumentNotFoundException.EXCEPTION)))
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return findDocumentPort.findRandom()
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocument(String text, PagingInfo pagingInfo) {
        return findDocumentPort.search(text, pagingInfo);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentsByTitle(String text, PagingInfo pagingInfo) {
        return findDocumentPort.searchByTitle(text, pagingInfo)
                .map(DocumentSearchResult::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentsByContent(String text, PagingInfo pagingInfo) {
        return findDocumentPort.searchByContent(text, pagingInfo);
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentsSortByMostRevision(PagingInfo pagingInfo) {
        return findDocumentPort.findAllSortByMostRevision(pagingInfo)
                .map(SimpleDocumentResponse::of);
    }

    @Override
    public Flux<GetMostViewDocumentResponse> getDocumentsSortByView(PagingInfo pagingInfo) {
        return findDocumentPort.findAllSortByView(pagingInfo)
                .map(GetMostViewDocumentResponse::of);
    }

}
