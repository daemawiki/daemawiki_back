package com.example.daemawiki.domain.file.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteFileRequest(
        @NotBlank
        String fileName
) {
}
