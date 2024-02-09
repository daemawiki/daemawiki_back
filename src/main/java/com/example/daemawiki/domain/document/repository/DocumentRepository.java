package com.example.daemawiki.domain.document.repository;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentRepository extends ReactiveMongoRepository<DefaultDocument, String> {
    Mono<DefaultDocument> findByTitle(String title);

    Flux<DefaultDocument> findAllByGroupsContains(String group);

}
