package org.daemawiki.domain.document.usecase;

import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.GetMostViewDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetDocumentUsecase {
    Mono<GetDocumentResponse> getDocumentById(String id);
    Mono<GetDocumentResponse> getDocumentByRandom();
    Flux<DocumentSearchResult> searchDocument(String text, PagingInfo pagingInfo);
    Flux<DocumentSearchResult> searchDocumentTitle(String text, PagingInfo pagingInfo);
    Flux<DocumentSearchResult> searchDocumentContent(String text, PagingInfo pagingInfo);
    Flux<SimpleDocumentResponse> getDocumentsMostRevision(PagingInfo pagingInfo);
    Flux<GetMostViewDocumentResponse> getDocumentOrderByView(PagingInfo pagingInfo);

}
