package org.daemawiki.utils;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class MongoQueryUtils {
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public MongoQueryUtils(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    public <T> Flux<T> find(Query query, Class<T> targetClass) {
        return reactiveMongoTemplate.find(query, targetClass);
    }

}
