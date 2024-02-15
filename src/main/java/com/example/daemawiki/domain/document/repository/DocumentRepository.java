package com.example.daemawiki.domain.document.repository;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentRepository extends ReactiveMongoRepository<DefaultDocument, String> {
    @Query("{'$or':[{'title':{$regex:'?0', $options:'i'}}, {'content':{$regex:'?0', $options:'i'}}]}")
    Flux<DefaultDocument> findByTextContaining(String text);

    @Aggregation("{ $sample: {'size':  1} }")
    Mono<DefaultDocument> findRandomDocument();
}
