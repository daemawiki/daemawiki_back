package com.example.daemawiki.domain.mail.dto;

public record AuthCodeVerifyRequest(
        String mail,
        String authCode
) {
}
