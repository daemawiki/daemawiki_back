package com.example.daemawiki.domain.editor.dto;

import jakarta.validation.constraints.NotBlank;

public record AddEditorRequest(
        @NotBlank(message = "문서의 id를 입력해주세요.")
        String documentId,
        @NotBlank(message = "문서 변경 권한을 줄 유저의 이메일을 입력해주세요.")
        String email) {
}
