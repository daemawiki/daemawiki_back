package org.daemawiki.domain.user_auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReissueRequest(
        @NotBlank(message = "토큰이 비어있습니다.")
        String token
) {
}
