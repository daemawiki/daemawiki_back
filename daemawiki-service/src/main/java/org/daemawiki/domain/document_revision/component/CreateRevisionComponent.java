package org.daemawiki.domain.document_revision.component;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document_revision.model.RevisionDetail;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CreateRevisionComponent {
    Mono<Void> create(DefaultDocument document, RevisionType revisionType, List<RevisionDetail> data);

}
