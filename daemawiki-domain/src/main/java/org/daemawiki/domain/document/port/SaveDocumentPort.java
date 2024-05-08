package org.daemawiki.domain.document.port;

import org.daemawiki.domain.document.model.DefaultDocument;
import reactor.core.publisher.Mono;

public interface SaveDocumentPort {
    Mono<DefaultDocument> save(DefaultDocument document);
    Mono<DefaultDocument> increaseView(DefaultDocument document);

}
