package com.example.daemawiki.domain.content.dto;

public record WriteContentRequest(
        String documentId,
        String index,
        String content
) {
}
