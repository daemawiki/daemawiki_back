package com.example.daemawiki.domain.document.dto.request;

import java.util.List;

public record AddDocumentGroupRequest(
        String documentId,
        List<String> groups
) {
}
