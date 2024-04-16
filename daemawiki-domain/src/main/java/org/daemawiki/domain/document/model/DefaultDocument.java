package org.daemawiki.domain.document.model;

import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.editor.model.DocumentEditor;
import org.daemawiki.domain.info.model.Info;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Setter
@Getter
@Document
@NoArgsConstructor
public class DefaultDocument {

    @Id
    private String id;

    private String title;

    private DocumentType type;

    private EditDateTime dateTime;

    private Info info;

    private List<List<String>> groups;

    private DocumentEditor editor;

    private List<Content> contents = Lists.mutable.of();

    private Long view = 0L;

    private Long version = 0L;

    public static DefaultDocument create(String title, DocumentType type, EditDateTime dateTime, Info info, List<List<String>> groups, DocumentEditor editor, List<Content> content) {
        return DefaultDocument.builder()
                .title(title)
                .type(type)
                .dateTime(dateTime)
                .info(info)
                .groups(groups)
                .editor(editor)
                .contents(content)
                .build();
    }

    @Builder
    public DefaultDocument(String title, DocumentType type, EditDateTime dateTime, Info info, List<List<String>> groups, DocumentEditor editor, List<Content> contents) {
        this.title = title;
        this.type = type;
        this.dateTime = dateTime;
        this.info = info;
        this.groups = groups;
        this.editor = editor;
        this.contents = contents;
    }

    public void increaseView() {
        this.view++;
    }

    public void increaseVersion() {
        this.version++;
    }

    public void update(String title, DocumentType type, List<List<String>> groups) {
        this.title = title;
        this.type = type;
        this.groups = groups;
    }

    public void updateByUserEdit(String title, List<List<String>> groups) {
        this.title = title;
        this.groups = groups;
    }

}
