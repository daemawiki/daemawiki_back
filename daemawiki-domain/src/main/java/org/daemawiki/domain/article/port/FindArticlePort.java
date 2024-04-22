package org.daemawiki.domain.article.port;

import org.daemawiki.domain.article.model.Article;
import org.daemawiki.utils.PagingInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindArticlePort {
    Flux<Article> findAll(PagingInfo pagingInfo);
    Flux<Article> search(String keyword, PagingInfo pagingInfo);
    Mono<Article> findById(String articleId);

}
