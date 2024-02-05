package com.example.daemawiki.domain.revision.model;

import com.example.daemawiki.domain.revision.model.type.RevisionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
@NoArgsConstructor
public class RevisionHistory {

    @Id
    private String id;

    private RevisionType type;

    private String documentId;

    private String title;

    private String editor;

    private String updatedDateTime;

    @Builder
    public RevisionHistory(RevisionType type, String documentId, String title, String editor, String updatedDateTime) {
        this.type = type;
        this.documentId = documentId;
        this.title = title;
        this.editor = editor;
        this.updatedDateTime = updatedDateTime;
    }
    
}
