package org.daemawiki.domain.document.usecase;

import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.GetMostViewDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetDocumentUsecase {
    Mono<GetDocumentResponse> getDocumentById(String id);
    Mono<GetDocumentResponse> getDocumentByRandom();
    Flux<DocumentSearchResult> searchDocument(String text);
    Flux<DocumentSearchResult> searchDocumentTitle(String text);
    Flux<DocumentSearchResult> searchDocumentContent(String text);
    Flux<SimpleDocumentResponse> getDocumentMostRevisionTop10();
    Flux<SimpleDocumentResponse> getDocumentsMostRevision();
    Flux<GetMostViewDocumentResponse> getDocumentOrderByView();

}
