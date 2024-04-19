package org.daemawiki.domain.document.application;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindDocumentPort {
    Mono<DefaultDocument> getDocumentById(String id);
    Mono<DefaultDocument> getDocumentByRandom();
    Flux<DocumentSearchResult> searchDocument(String text, PagingInfo pagingInfo);
    Flux<DefaultDocument> searchDocumentTitle(String text, PagingInfo pagingInfo);
    Flux<DocumentSearchResult> searchDocumentContent(String text, PagingInfo pagingInfo);
    Flux<DefaultDocument> getDocumentTop10();
    Flux<DefaultDocument> getDocumentMostRevision(PagingInfo pagingInfo);
    Flux<DefaultDocument> getDocumentOrderByView(PagingInfo pagingInfo);
    Flux<DefaultDocument> getDocumentOrderByViewTop10();

}
