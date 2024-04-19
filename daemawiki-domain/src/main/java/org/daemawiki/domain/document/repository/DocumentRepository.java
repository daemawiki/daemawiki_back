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
                    "] } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<DocumentSearchResult> findByTextContaining(String text, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $match: { 'title': '?0' } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<DefaultDocument> findByTitleContaining(String text, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $unwind: '$contents' }",
            "{ $match: { 'contents.detail': { $regex: '?0', $options: 'i' } } }",
            "{ $sort: { ?1: ?2 } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<DocumentSearchResult> findByContentTextContaining(String text, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation("{ $sample: {'size':  1} }")
    Mono<DefaultDocument> findRandomDocument();

    Flux<DefaultDocument> findTop10ByOrderByVersionDesc();

    @Aggregation(pipeline = {
            "{ $sort: { 'view': ?0 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    Flux<DefaultDocument> findAllByOrderByView(Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $sort: { 'version': ?0 } }",
            "{ $skip: ?1 }",
            "{ $limit: ?2 }"
    })
    Flux<DefaultDocument> findAllByOrderByVersion(Integer sortDirection, Integer skip, Integer limit);

    Flux<DefaultDocument> findTop10ByOrderByViewDesc();

    default Mono<DefaultDocument> increaseView(DefaultDocument document) {
        return Mono.justOrEmpty(document)
                .doOnNext(DefaultDocument::increaseView)
                .flatMap(this::save);
    }

}
