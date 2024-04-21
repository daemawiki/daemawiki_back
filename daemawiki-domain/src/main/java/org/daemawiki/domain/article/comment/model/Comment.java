package org.daemawiki.domain.article.comment.model;

import lombok.Getter;
import lombok.Setter;
import org.daemawiki.domain.user.model.Writer;
import org.eclipse.collections.api.factory.Lists;

import java.util.List;

@Getter
public class Comment {
    private final String index; // parent: 1, 1's child : 1.x
    private final String content;
    private final Writer writer;
    @Setter
    private Integer parentIndex = null;
    private List<Comment> childComments = Lists.mutable.of();

    public void addChildComment(Comment comment) {
        childComments.add(comment);
    }

    public void removeChildComment(Comment comment) {
        childComments.remove(comment);
    }

    private Comment(String index, String content, Writer writer) {
        this.index = index;
        this.content = content;
        this.writer = writer;
    }

    public static Comment create(String index, String content, Writer writer) {
        return new Comment(index, content, writer);
    }

}
