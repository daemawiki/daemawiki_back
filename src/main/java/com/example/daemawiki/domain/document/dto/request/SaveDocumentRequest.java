package com.example.daemawiki.domain.document.dto.request;

import java.util.List;

public record SaveDocumentRequest(
        String title,
        String type,
        String content,
        List<List<String>> groups
) {
}
