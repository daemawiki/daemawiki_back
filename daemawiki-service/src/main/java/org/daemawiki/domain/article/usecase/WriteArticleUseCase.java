package org.daemawiki.domain.article.usecase;

import org.daemawiki.domain.article.dto.WriteArticleRequest;
import reactor.core.publisher.Mono;

public interface WriteArticleUseCase {
    Mono<Void> write(WriteArticleRequest request);

}
