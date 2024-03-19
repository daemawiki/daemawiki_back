package org.daemawiki.domain.document.application;

import org.daemawiki.domain.document.model.DefaultDocument;
import reactor.core.publisher.Mono;

public interface SaveDocumentPort {
    Mono<DefaultDocument> save(DefaultDocument document);

}
