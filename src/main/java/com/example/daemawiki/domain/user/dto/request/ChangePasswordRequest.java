package com.example.daemawiki.domain.user.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        //String oldPassword,
        @NotBlank
        String newPassword,
        @Nullable
        String email
) {
}
