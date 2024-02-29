package com.example.daemawiki.domain.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WriteContentRequest(
        @NotBlank(message = "문서의 id를 입력해주세요.")
        String documentId,
        @NotBlank(message = "내용을 추가하고 싶은 index를 입력해주세요.")
        String index,
        @NotNull(message = "null은 내용이 될 수 없습니다.")
        String content,
        @NotNull(message = "문서의 버전을 입력해주세요.")
        Integer version
) {
}
