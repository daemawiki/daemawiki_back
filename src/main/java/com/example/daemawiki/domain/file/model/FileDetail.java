package com.example.daemawiki.domain.file.model;

import com.example.daemawiki.domain.file.model.type.FileType;
import lombok.Builder;

@Builder
public record FileDetail(
        FileType type,
        String url
) {
}
