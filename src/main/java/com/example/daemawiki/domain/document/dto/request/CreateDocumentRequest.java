package com.example.daemawiki.domain.document.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDocumentRequest(
        @NotBlank(message = "문서의 제목을 입력해주세요.")
        String title,
        @NotBlank(message = "문서의 타입을 선택해주세요.")
        String type
) {
}
