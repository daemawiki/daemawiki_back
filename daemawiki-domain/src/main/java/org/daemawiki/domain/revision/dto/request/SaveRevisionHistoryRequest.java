package org.daemawiki.domain.revision.dto.request;

import org.daemawiki.domain.revision.model.type.RevisionType;
import lombok.Builder;

@Builder
public record SaveRevisionHistoryRequest(
        RevisionType type,
        String documentId,
        String title
) {

    public static SaveRevisionHistoryRequest create(RevisionType type, String documentId, String title) {
        return SaveRevisionHistoryRequest.builder()
                .type(type)
                .documentId(documentId)
                .title(title)
                .build();
    }

}
