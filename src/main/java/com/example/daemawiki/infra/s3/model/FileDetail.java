package com.example.daemawiki.infra.s3.model;

import com.example.daemawiki.infra.s3.model.type.ImageType;
import lombok.Builder;

@Builder
public record FileDetail(
        ImageType type,
        String url
) {
}
