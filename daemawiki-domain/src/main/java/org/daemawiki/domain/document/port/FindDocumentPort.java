package org.daemawiki.domain.document.port;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindDocumentPort {
    Mono<DefaultDocument> findById(String id);
    Mono<DefaultDocument> findRandom();
    Flux<DocumentSearchResult> search(String text, PagingInfo pagingInfo);
    Flux<DefaultDocument> searchByTitle(String text, PagingInfo pagingInfo);
    Flux<DocumentSearchResult> searchByContent(String text, PagingInfo pagingInfo);
    Flux<DefaultDocument> findAllSortByMostRevision(PagingInfo pagingInfo);
    Flux<DefaultDocument> findAllSortByView(PagingInfo pagingInfo);

}
