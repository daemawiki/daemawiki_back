package com.example.daemawiki.domain.auth.dto;

public record SignupRequest(
        String nickname,
        String email,
        String password
) {
}
