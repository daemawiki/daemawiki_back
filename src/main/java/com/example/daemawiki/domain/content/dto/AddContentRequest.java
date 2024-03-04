package com.example.daemawiki.domain.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddContentRequest(
        @NotBlank(message = "추가할 목차의 인덱스를 입력해주세요.")
        String index,
        @NotBlank(message = "추가할 목차의 제목을 입력해주세요.")
        String title,
        @NotNull(message = "문서의 버전을 입력해주세요.")
        Integer version
) {
}
