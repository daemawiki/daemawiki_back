package org.daemawiki.domain.article.adapter;

import org.daemawiki.domain.article.model.Article;
import org.daemawiki.domain.article.port.DeleteArticlePort;
import org.daemawiki.domain.article.port.FindArticlePort;
import org.daemawiki.domain.article.port.SaveArticlePort;
import org.daemawiki.domain.article.repository.ArticleRepository;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ArticleAdapter implements FindArticlePort, SaveArticlePort, DeleteArticlePort {
    private final ArticleRepository articleRepository;

    public ArticleAdapter(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Mono<Void> delete(String articleId) {
        return articleRepository.deleteById(articleId);
    }

    @Override
    public Flux<Article> findAll(PagingInfo pagingInfo) {
        return articleRepository.findAll(
                pagingInfo.sortBy(),
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
                );
    }

    @Override
    public Flux<Article> search(String keyword, PagingInfo pagingInfo) {
        return articleRepository.search(
                keyword,
                pagingInfo.sortBy(),
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
                );
    }

    @Override
    public Mono<Article> findById(String articleId) {
        return articleRepository.findById(articleId);
    }

    @Override
    public Mono<Article> save(Article article) {
        return articleRepository.save(article);
    }

}
