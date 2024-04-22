package org.daemawiki.domain.article.dto;

public record WriteArticleRequest(
        String title,
        String content
) {
}
