package org.daemawiki.domain.article.service;

import org.daemawiki.domain.article.dto.WriteArticleRequest;
import org.daemawiki.domain.article.model.Article;
import org.daemawiki.domain.article.port.SaveArticlePort;
import org.daemawiki.domain.article.usecase.WriteArticleUseCase;
import org.daemawiki.domain.user.application.FindUserPort;
import org.daemawiki.domain.user.model.Writer;
import org.daemawiki.exception.h403.NoPermissionUserException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class WriteArticleService implements WriteArticleUseCase {
    private final FindUserPort findUserPort;
    private final SaveArticlePort saveArticlePort;

    public WriteArticleService(FindUserPort findUserPort, SaveArticlePort saveArticlePort) {
        this.findUserPort = findUserPort;
        this.saveArticlePort = saveArticlePort;
    }

    @Override
    public Mono<Void> write(WriteArticleRequest request) {
        return findUserPort.currentUser()
                .filter(user -> !user.getIsBlocked())
                .switchIfEmpty(Mono.defer(() -> Mono.error(NoPermissionUserException.EXCEPTION)))
                .flatMap(user -> {
                    Article article = Article.create(request.title(), request.content(), Writer.of(user));
                    return saveArticlePort.save(article);
                })
                .then();
    }

}
