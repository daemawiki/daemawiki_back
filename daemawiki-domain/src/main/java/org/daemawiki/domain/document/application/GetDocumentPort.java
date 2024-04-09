package org.daemawiki.domain.document.application;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetDocumentPort {
    Mono<DefaultDocument> getDocumentById(String id);
    Mono<DefaultDocument> getDocumentByRandom();
    Flux<DocumentSearchResult> searchDocument(String text);
    Flux<DefaultDocument> searchDocumentTitle(String text);
    Flux<DocumentSearchResult> searchDocumentContent(String text);
    Flux<DefaultDocument> getDocumentTop10();
    Flux<DefaultDocument> getDocumentMostRevision();
    Flux<DefaultDocument> getDocumentOrderByView();

}
