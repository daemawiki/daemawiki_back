package com.example.daemawiki.domain.file.model;

import lombok.Builder;

@Builder
public record FileResponse(
        String fileName,
        String fileType,
        FileDetail detail

) {
}
