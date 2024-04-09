package org.daemawiki.domain.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.daemawiki.domain.document.model.DefaultDocument;

import java.time.LocalDateTime;

@Builder
public record GetMostViewDocumentResponse(
        String id,
        String title,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime updatedDate,
        Long view
) {

    public static GetMostViewDocumentResponse of(DefaultDocument document) {
        return GetMostViewDocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .updatedDate(document.getDateTime().getUpdated())
                .view(document.getView())
                .build();
    }

}
