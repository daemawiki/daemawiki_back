package com.example.daemawiki.domain.document.dto.request;

public record UpdateDocumentRequest(
        String documentId,
        String title,
        String type,
        String content
) {
}
