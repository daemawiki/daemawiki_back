package com.example.daemawiki.domain.document.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record SaveDocumentRequest(
        @NotBlank(message = "문서 제목이 비어있습니다.")
        String title,
        @NotBlank(message = "문서의 유형이 비어있습니다.")
        String type,
        @NotBlank(message = "문서의 내용이 비어있습니다.")
        String content,
        @NotNull(message = "문서의 분류가 비어있습니다.")
        List<List<String>> groups,
        @Nullable
        Integer version
) {
}
