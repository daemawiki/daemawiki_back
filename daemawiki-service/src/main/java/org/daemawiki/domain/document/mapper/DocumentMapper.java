package org.daemawiki.domain.document.mapper;

import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DefaultDocument;
import reactor.core.publisher.Mono;

public interface DocumentMapper {
    Mono<GetDocumentResponse> defaultDocumentToGetResponse(DefaultDocument document);
    Mono<SimpleDocumentResponse> defaultDocumentToSimpleDocumentResponse(DefaultDocument document);
}
