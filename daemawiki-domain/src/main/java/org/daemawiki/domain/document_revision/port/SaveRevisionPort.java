package org.daemawiki.domain.document_revision.port;

import org.daemawiki.domain.document_revision.model.RevisionHistory;
import reactor.core.publisher.Mono;

public interface SaveRevisionPort {
    Mono<RevisionHistory> save(RevisionHistory revisionHistory);

}
