package org.daemawiki.domain.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SimpleDocumentResponse(
        String id,
        String title,
        Integer numberOfUpdate,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime updatedDate,
        Integer view
) {
}
