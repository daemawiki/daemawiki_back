package org.daemawiki.domain.revision.application;

import org.daemawiki.domain.revision.model.RevisionHistory;
import reactor.core.publisher.Mono;

public interface SaveRevisionPort {
    Mono<RevisionHistory> save(RevisionHistory revisionHistory);

}
