package com.example.daemawiki.domain.user.dto;

import com.example.daemawiki.domain.file.model.File;
import lombok.Builder;

@Builder
public record GetUserResponse(
        String userId,
        String documentId,
        String name,
        String major,
        File profile
) {
}
