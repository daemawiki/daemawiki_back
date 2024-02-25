package com.example.daemawiki.domain.document.dto.response;

import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import lombok.Builder;

@Builder
public record SearchDocumentResponse(
        String id,
        String title,
        DocumentType type,
        EditDateTime dateTime,
        String content
) {
}
