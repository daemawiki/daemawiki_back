package org.daemawiki.domain.article.model;

import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.user.model.Writer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document
public class Article {

    @Id
    private String id;

    private String title;

    private String content;

    private Writer writer;

    @CreatedDate
    private LocalDateTime createdAt;

    private Long recommend = 0L;

    private Long view = 0L;

    protected Article() {}

    @Builder
    public Article(String title, String content, Writer writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Article of(String title, String content, Writer writer) {
        return new Article(title, content, writer);
    }

    public void increaseView() {
        view++;
    }

    public void increaseRecommend() {
        recommend++;
    }

}