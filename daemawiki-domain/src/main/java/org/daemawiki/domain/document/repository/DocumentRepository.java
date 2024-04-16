package org.daemawiki.domain.document.repository;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
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

    Flux<DefaultDocument> findByTitleContaining(String text);

    @Aggregation(pipeline = {
            "{ $unwind: '$contents' }",
            "{ $match: { $or: [ " +
                    "{ 'contents.detail': { $regex: '?0', $options: 'i' } }" +
                    "] } }"
    })
    Flux<DocumentSearchResult> findByContentTextContaining(String text);

    @Aggregation("{ $sample: {'size':  1} }")
    Mono<DefaultDocument> findRandomDocument();

    Flux<DefaultDocument> findTop10ByOrderByVersionDesc();

    Flux<DefaultDocument> findAllByOrderByViewDesc();

    Flux<DefaultDocument> findAllByOrderByVersionDesc();

    Flux<DefaultDocument> findTop10ByOrderByViewDesc();

    default Mono<DefaultDocument> increaseView(DefaultDocument document) {
        return Mono.justOrEmpty(document)
                .doOnNext(DefaultDocument::increaseView)
                .flatMap(this::save);
    }

}
