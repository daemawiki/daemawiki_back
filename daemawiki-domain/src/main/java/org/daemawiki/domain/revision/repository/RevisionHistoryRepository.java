package org.daemawiki.domain.revision.repository;

import org.daemawiki.domain.revision.model.RevisionHistory;
import org.daemawiki.domain.revision.model.type.RevisionType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RevisionHistoryRepository extends ReactiveMongoRepository<RevisionHistory, String> {
    Flux<RevisionHistory> findAllByTypeInOrderByCreatedDateTimeDesc(List<RevisionType> types);

    Flux<RevisionHistory> findAllByOrderByCreatedDateTimeDesc();

    Flux<RevisionHistory> findAllByDocumentId(String id);

    Flux<RevisionHistory> findAllByEditor_IdOrderByCreatedDateTimeDesc(String editor);

}
