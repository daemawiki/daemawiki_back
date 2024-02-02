package com.example.daemawiki.domain.mail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthCodeVerifyRequest(
        @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(dsm\\.hs\\.kr)$",
                message = "메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "인가 받을 메일을 입력해주세요.")
        String mail,
        @Size(max = 6, min = 6, message = "인증 코드는 6글자입니다.")
        @NotBlank(message = "인증 코드를 입력해주세요.")
        String authCode
) {
}
