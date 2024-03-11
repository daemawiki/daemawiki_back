package com.example.daemawiki.domain.document.repository;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.model.DocumentSearchResult;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DocumentRepository extends ReactiveMongoRepository<DefaultDocument, String> {
    @Aggregation(pipeline = {
            "{ $unwind: '$contents' }",
            "{ $match: { $or: [ " +
                    "{ 'title': { $regex: '?0', $options: 'i' } }, " +
                    "{ 'contents.detail': { $regex: '?0', $options: 'i' } }" +
                    "] } }"
    })
    Flux<DocumentSearchResult> findByTextContaining(String text);

    @Aggregation("{ $sample: {'size':  1} }")
    Mono<DefaultDocument> findRandomDocument();

    Flux<DefaultDocument> findTop10ByOrderByVersionDesc();

    Flux<DefaultDocument> findAllByOrderByViewDesc();
}
