package org.daemawiki.domain.document.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import org.daemawiki.domain.document.model.DefaultDocument;

import java.time.LocalDateTime;

@Builder
public record SimpleDocumentResponse(
        String id,
        String title,
        Long numberOfUpdate,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "Asia/Seoul")
        LocalDateTime updatedDate,
        Long view
) {

        public static SimpleDocumentResponse of(DefaultDocument document) {
                return SimpleDocumentResponse.builder()
                        .id(document.getId())
                        .title(document.getTitle())
                        .numberOfUpdate(document.getVersion())
                        .updatedDate(document.getDateTime().getUpdated())
                        .view(document.getView())
                        .build();
        }

}
