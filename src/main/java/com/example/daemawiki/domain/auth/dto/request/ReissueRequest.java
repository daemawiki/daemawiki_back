package com.example.daemawiki.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;

public record ReissueRequest(
        @NotNull(message = "데이터가 비어있습니다.")
        String token
) {
}
