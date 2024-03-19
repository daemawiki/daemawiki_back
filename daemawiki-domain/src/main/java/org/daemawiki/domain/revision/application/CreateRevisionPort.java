package org.daemawiki.domain.revision.application;

import org.daemawiki.domain.revision.model.RevisionHistory;
import reactor.core.publisher.Mono;

public interface CreateRevisionPort {
    Mono<Void> save(RevisionHistory revisionHistory);

}
