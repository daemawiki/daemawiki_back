package com.example.daemawiki.domain.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
