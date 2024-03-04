package com.example.daemawiki.domain.content.dto;

public record EditContentTableTitleRequest(
        String index,
        String newTitle,
        Integer version
) {
}
