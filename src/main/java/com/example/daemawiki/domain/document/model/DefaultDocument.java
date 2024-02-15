package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Document
@NoArgsConstructor
public class DefaultDocument {

    @Id
    private String id;

    private String title;

    private DocumentType type;

    private EditDateTime dateTime;

    private List<String> groups;

    private DocumentEditor editor;

    private String content;

    @Version
    private Integer version;

    @Builder
    public DefaultDocument(String title, DocumentType type, EditDateTime dateTime, List<String> groups, DocumentEditor documentEditor, String content) {
        this.title = title;
        this.type = type;
        this.dateTime = dateTime;
        this.groups = groups;
        this.editor = documentEditor;
        this.content = content;
    }

    public void update(String title, DocumentType type, String content, List<String> groups) {
        this.title = title;
        this.type = type;
        this.content = content;
        this.groups = groups;
    }

}
