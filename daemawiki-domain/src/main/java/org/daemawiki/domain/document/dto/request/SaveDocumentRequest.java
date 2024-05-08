package org.daemawiki.domain.document.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document_revision.model.RevisionDetail;

import java.util.List;

@Builder
public record SaveDocumentRequest(
        @NotBlank(message = "문서 제목이 비어있습니다.")
        String title,
        @NotBlank(message = "문서의 유형이 비어있습니다.")
        String type,
        @Nullable
        Content content,
        @NotNull(message = "문서의 분류가 비어있습니다.")
        List<List<String>> groups,
        @Nullable
        Long version,
        @Nullable // TODO: 5/7/24 수정 내역 필드
        List<RevisionDetail> data
) {

        public static SaveDocumentRequest create(String title, String type, List<List<String>> groups) {
                return SaveDocumentRequest.builder()
                        .title(title)
                        .type(type)
                        .groups(groups)
                        .build();
        }

}
