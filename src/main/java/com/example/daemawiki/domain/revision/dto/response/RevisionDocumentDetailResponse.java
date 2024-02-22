package com.example.daemawiki.domain.revision.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RevisionDocumentDetailResponse(
        String documentId,
        String title,
        Integer numberOfRevision,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime updatedDate
) {
}
