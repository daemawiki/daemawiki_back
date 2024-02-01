package com.example.daemawiki.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "데이터가 비어있습니다.")
        String email,
        @NotNull(message = "데이터가 비어있습니다.")
        String password
) {
}
