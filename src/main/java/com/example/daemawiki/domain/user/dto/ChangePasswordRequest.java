package com.example.daemawiki.domain.user.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {
}
