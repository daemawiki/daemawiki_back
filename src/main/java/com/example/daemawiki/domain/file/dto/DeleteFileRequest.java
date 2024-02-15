package com.example.daemawiki.domain.file.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteFileRequest(
        @NotBlank(message = "삭제할 파일의 이름을 작성해주세요.")
        String key
) {
}
