package com.example.daemawiki.domain.document.dto.response;

import com.example.daemawiki.domain.document.model.DocumentEditor;
import com.example.daemawiki.domain.document.model.Group;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import lombok.Builder;

import java.util.List;

@Builder
public record GetDocumentResponse(
        String title,
        DocumentType type,
        EditDateTime dateTime,
        List<Group> groups,
        DocumentEditor editor

) {
}
