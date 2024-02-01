package com.example.daemawiki.domain.mail.dto;

import jakarta.validation.constraints.NotNull;

public record AuthCodeVerifyRequest(
        @NotNull(message = "인가 받을 메일을 입력해주세요")
        String mail,
        @NotNull(message = "인증번호를 입력해주세요.")
        String authCode
) {
}
