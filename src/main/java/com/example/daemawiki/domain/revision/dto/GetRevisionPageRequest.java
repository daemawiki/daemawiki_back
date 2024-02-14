package com.example.daemawiki.domain.revision.dto;

import lombok.NonNull;

public record GetRevisionPageRequest(
        @NonNull
        String lastRevisionId
) {
}
