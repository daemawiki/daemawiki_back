package org.daemawiki.domain.article.port;

import org.daemawiki.domain.article.model.Article;
import reactor.core.publisher.Mono;

public interface SaveArticlePort {
    Mono<Article> save(Article article);

}
