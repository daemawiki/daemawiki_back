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
    Flux<DocumentSearchResult> searchDocument(String text, String lastDocumentId);
    Flux<DocumentSearchResult> searchDocumentTitle(String text, String lastDocumentId);
    Flux<DocumentSearchResult> searchDocumentContent(String text, String lastDocumentId);
    Flux<SimpleDocumentResponse> getDocumentMostRevisionTop10();
    Flux<SimpleDocumentResponse> getDocumentsMostRevision(String lastDocumentId);
    Flux<GetMostViewDocumentResponse> getDocumentOrderByView(String lastDocumentId);
    Flux<GetMostViewDocumentResponse> getDocumentOrderByViewTop10();

}
