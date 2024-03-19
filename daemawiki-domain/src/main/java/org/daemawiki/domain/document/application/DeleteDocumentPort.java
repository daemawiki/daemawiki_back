package org.daemawiki.domain.document.application;

import org.daemawiki.domain.document.model.DefaultDocument;
import reactor.core.publisher.Mono;

public interface DeleteDocumentPort {
    Mono<Void> deleteById(String id);
    Mono<Void> delete(DefaultDocument document);

}
