package org.daemawiki.domain.article.port;

import reactor.core.publisher.Mono;

public interface DeleteArticlePort {
    Mono<Void> delete(String articleId);

}
