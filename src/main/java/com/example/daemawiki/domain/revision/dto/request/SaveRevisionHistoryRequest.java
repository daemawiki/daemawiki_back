package com.example.daemawiki.domain.revision.dto.request;

import com.example.daemawiki.domain.revision.model.type.RevisionType;
import lombok.Builder;

@Builder
public record SaveRevisionHistoryRequest(
        RevisionType type,
        String documentId,
        String title
) {
}
