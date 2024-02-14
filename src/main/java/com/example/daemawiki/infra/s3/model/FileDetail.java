package com.example.daemawiki.infra.s3.model;

import com.example.daemawiki.infra.s3.model.type.FileType;
import lombok.Builder;

@Builder
public record FileDetail(
        FileType type,
        String url
) {
}
