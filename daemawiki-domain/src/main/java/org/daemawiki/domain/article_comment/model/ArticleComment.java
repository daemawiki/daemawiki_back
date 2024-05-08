package org.daemawiki.domain.article_comment.model;

import org.daemawiki.domain.user.model.Writer;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
public class ArticleComment {

    @Id
    private String id;
    private String content;
    private Writer writer;

    @CreatedDate
    private LocalDateTime createdAt;

}
