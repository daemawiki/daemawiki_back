package com.example.daemawiki.domain.auth.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token
) {
}
