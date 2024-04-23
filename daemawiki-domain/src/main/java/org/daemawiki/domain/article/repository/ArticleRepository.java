package org.daemawiki.domain.article.repository;

import org.daemawiki.domain.article.model.Article;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ArticleRepository extends ReactiveMongoRepository<Article, String> {
    @Aggregation(pipeline = {
            "{ $match: { $or: [ " +
                    "{ 'title': { $regex: '?0', $options: 'i' } }, " +
                    "{ 'content': { $regex: '?0', $options: 'i' } }" +
                    "] } }",
            "{ $sort: { ?1: '?2' } }",
            "{ $skip: ?3 }",
            "{ $limit: ?4 }"
    })
    Flux<Article> search(String keyword, String sortBy, Integer sortDirection, Integer skip, Integer limit);

    @Aggregation(pipeline = {
            "{ $sort: { ?0: '?1' } }",
            "{ $skip: ?2 }",
            "{ $limit: ?3 }"
    })
    Flux<Article> findAll(String sortBy, Integer sortDirection, Integer skip, Integer limit);

}
