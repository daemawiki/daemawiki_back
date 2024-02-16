package com.example.daemawiki.domain.revision.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RevisionDocumentDetailResponse(
        String documentId,
        String title,
        Integer numberOfRevision,
        LocalDateTime updatedDate
) {
}
