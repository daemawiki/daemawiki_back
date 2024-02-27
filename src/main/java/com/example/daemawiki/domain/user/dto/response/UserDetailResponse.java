package com.example.daemawiki.domain.user.dto.response;

import com.example.daemawiki.domain.file.model.File;
import lombok.Builder;

@Builder
public record UserDetailResponse(
        String id,
        String name,
        File profile
) {
}