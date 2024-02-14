package com.example.daemawiki.infra.s3.model;

import lombok.Builder;

@Builder
public record ImageResponse(
        String fileName,
        String fileType,
        ImageDetail detail

) {
}
