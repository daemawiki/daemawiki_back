package org.daemawiki.domain.article.model;

import lombok.Getter;
import org.daemawiki.domain.user.model.User;
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

    public void increaseView() {
        this.view++;
    }

    public void increaseRecommend() {
        this.recommend++;
    }

    public Article(String title, String content, Writer writer) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public static Article create(String title, String content, User user) {
        return new Article(title, content, Writer.of(user));
    }

}