package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.eclipse.collections.api.factory.Lists;
import org.springframework.data.annotation.Id;
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

    private List<Group> groups = Lists.mutable.of();

    private DocumentEditor editor;

    private String content;

    @Builder
    public DefaultDocument(String title, DocumentType type, EditDateTime dateTime, List<Group> groups, DocumentEditor documentEditor, String content) {
        this.title = title;
        this.type = type;
        this.dateTime = dateTime;
        this.groups = groups;
        this.editor = documentEditor;
        this.content = content;
    }

    public void deleteGroup(Group group) {
        this.getGroups().remove(group);
    }

    public Boolean isContain(Group group) {
        return this.getGroups().contains(group);
    }

    public void update(String title, DocumentType type, String content) {
        this.title = title;
        this.type = type;
        this.content = content;
    }

}
