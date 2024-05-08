package org.daemawiki.domain.document_revision.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import org.daemawiki.domain.document_editor.model.Editor;
import org.daemawiki.domain.document_revision.model.type.RevisionType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Document
public class RevisionHistory {

    @Id
    @JsonIgnore
    private String id;

    private RevisionType type;

    private String documentId;

    private Long version;

    private String title;

    private Editor editor;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
    private LocalDateTime createdDateTime;

    private List<RevisionDetail> data = List.of();

    protected RevisionHistory() {}

    @Builder
    public RevisionHistory(RevisionType type, String documentId, Long version, String title, Editor editor, LocalDateTime createdDateTime, List<RevisionDetail> data) {
        this.type = type;
        this.documentId = documentId;
        this.version = version;
        this.title = title;
        this.editor = editor;
        this.createdDateTime = createdDateTime;
        this.data = data;
    }
    
}
