package com.example.daemawiki.domain.mail.dto;

import lombok.Builder;

@Builder
public record AuthCodeVerifyResponse(
        Boolean isSuccess,
        String message
) {
}
