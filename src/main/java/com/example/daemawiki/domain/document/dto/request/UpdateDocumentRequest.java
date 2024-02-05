package com.example.daemawiki.domain.document.dto.request;

import com.example.daemawiki.domain.document.model.type.DocumentType;

public record UpdateDocumentRequest(
        String documentId,
        String title,
        String type
) {
}
