package com.example.daemawiki.infra.s3.model;

import lombok.Builder;

@Builder
public record FileResponse(
        String fileName,
        String fileType,
        FileDetail detail

) {
}
