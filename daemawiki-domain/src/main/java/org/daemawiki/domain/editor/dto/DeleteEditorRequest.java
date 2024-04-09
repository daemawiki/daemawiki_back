package org.daemawiki.domain.editor.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteEditorRequest(
        @NotBlank(message = "문서 변경 권한을 삭제할 유저의 id를 입력해주세요.")
        String userId
) {
}
