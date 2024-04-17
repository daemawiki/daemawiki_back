package org.daemawiki.utils;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class MongoQueryUtils {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public MongoQueryUtils(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }
    // Mono<Page<T>> ? Flux<T> ?
    public <T> Flux<T> find(Query query, Class<T> targetClass) {
        return reactiveMongoTemplate.find(query, targetClass);
    }

}
