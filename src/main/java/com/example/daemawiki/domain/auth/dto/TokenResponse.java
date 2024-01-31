package com.example.daemawiki.domain.auth.dto;

import lombok.Builder;

@Builder
public record TokenResponse(
        String token
) {
}
