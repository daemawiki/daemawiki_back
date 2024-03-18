package org.daemawiki.domain.revision.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.daemawiki.domain.editor.model.Editor;
import org.daemawiki.domain.revision.model.type.RevisionType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Document
@NoArgsConstructor
public class RevisionHistory {

    @Id
    @JsonIgnore
    private String id;

    private RevisionType type;

    private String documentId;

    private String title;

    private Editor editor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
    private LocalDateTime createdDateTime;

    @Builder
    public RevisionHistory(RevisionType type, String documentId, String title, Editor editor, LocalDateTime createdDateTime) {
        this.type = type;
        this.documentId = documentId;
        this.title = title;
        this.editor = editor;
        this.createdDateTime = createdDateTime;
    }
    
}
