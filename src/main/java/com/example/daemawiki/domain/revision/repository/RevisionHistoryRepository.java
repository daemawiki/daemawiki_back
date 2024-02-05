package com.example.daemawiki.domain.revision.repository;

import com.example.daemawiki.domain.revision.model.RevisionHistory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RevisionHistoryRepository extends ReactiveMongoRepository<RevisionHistory, String> {
}
