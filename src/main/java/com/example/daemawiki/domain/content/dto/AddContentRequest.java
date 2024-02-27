package com.example.daemawiki.domain.content.dto;

public record AddContentRequest(
        String documentId,
        String index,
        String title
) {
}
