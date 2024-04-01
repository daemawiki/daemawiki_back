package org.daemawiki.domain.document.usecase;

import reactor.core.publisher.Mono;

public interface DeleteDocumentUsecase {
    Mono<Void> delete(String documentId);

}
