package com.example.daemawiki.domain.content.dto;

public record DeleteContentRequest(
        String index,
        Integer version
) {
}
