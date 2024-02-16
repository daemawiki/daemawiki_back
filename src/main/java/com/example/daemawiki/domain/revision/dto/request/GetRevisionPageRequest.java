package com.example.daemawiki.domain.revision.dto.request;

import lombok.NonNull;

public record GetRevisionPageRequest(
        @NonNull
        String lastRevisionId
) {
}
