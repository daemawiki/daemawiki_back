package org.daemawiki.domain.document_revision.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.daemawiki.domain.document_revision.model.RevisionHistory;
import org.daemawiki.domain.document_revision.model.type.RevisionType;

import java.time.LocalDateTime;

@Builder
public record GetRevisionByUserResponse(
        String id,
        RevisionType type,
        String documentId,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime createdDateTime
) {

    public static GetRevisionByUserResponse of(RevisionHistory revisionHistory) {
        return GetRevisionByUserResponse.builder()
                .id(revisionHistory.getId())
                .type(revisionHistory.getType())
                .documentId(revisionHistory.getDocumentId())
                .title(revisionHistory.getTitle())
                .createdDateTime(revisionHistory.getCreatedDateTime())
                .build();
    }

}
