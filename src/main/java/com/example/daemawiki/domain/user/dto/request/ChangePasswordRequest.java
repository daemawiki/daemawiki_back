package com.example.daemawiki.domain.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        //String oldPassword,
        @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
        String newPassword,
        @Nullable
        String email
) {
}
