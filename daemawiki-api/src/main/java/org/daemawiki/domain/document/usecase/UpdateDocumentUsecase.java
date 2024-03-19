package org.daemawiki.domain.document.usecase;

import org.daemawiki.domain.document.dto.request.SaveDocumentRequest;
import reactor.core.publisher.Mono;

public interface UpdateDocumentUsecase {
    Mono<Void> update(SaveDocumentRequest request, String documentId);

}
