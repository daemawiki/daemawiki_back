package com.example.daemawiki.domain.revision.repository;

import com.example.daemawiki.domain.revision.model.RevisionHistory;
import com.example.daemawiki.domain.revision.model.type.RevisionType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface RevisionHistoryRepository extends ReactiveMongoRepository<RevisionHistory, String> {
    Flux<RevisionHistory> findTop10ByTypeInOrderByCreatedDateTimeDesc(List<RevisionType> types);
    Flux<RevisionHistory> findAllByOrderByCreatedDateTimeDesc();

    Flux<RevisionHistory> findAllByDocumentId(String id);

}
