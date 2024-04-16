package org.daemawiki.domain.article.repository;

import org.daemawiki.domain.article.model.Article;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ArticleRepository extends ReactiveMongoRepository<Article, String> {
    @Aggregation(pipeline = {
            "{ $match: { $or: [ " +
                    "{ 'title': { $regex: '?0', $options: 'i' } }, " +
                    "{ 'contents': { $regex: '?0', $options: 'i' } }" +
                    "] } }"
    })
    Flux<Article> search(String keyword);

}
