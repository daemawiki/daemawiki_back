package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.content.model.Content;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.domain.editor.model.DocumentEditor;
import com.example.daemawiki.domain.info.model.Info;
import com.example.daemawiki.global.datetime.model.EditDateTime;
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

    private List<Info> info = Lists.mutable.of();

    private List<List<String>> groups;

    private DocumentEditor editor;

    private List<Content> contents;

    private Integer view = 0;

    private Integer version = 0;

    @Builder
    public DefaultDocument(String title, DocumentType type, EditDateTime dateTime, List<Info> info, List<List<String>> groups, DocumentEditor documentEditor, List<Content> content) {
        this.title = title;
        this.type = type;
        this.dateTime = dateTime;
        this.info = info;
        this.groups = groups;
        this.editor = documentEditor;
        this.contents = content;
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
