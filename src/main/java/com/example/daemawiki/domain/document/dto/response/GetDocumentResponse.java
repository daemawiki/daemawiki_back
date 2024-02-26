package com.example.daemawiki.domain.document.dto.response;

import com.example.daemawiki.domain.document.model.DocumentEditor;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.domain.info.model.Info;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import lombok.Builder;

import java.util.List;

@Builder
public record GetDocumentResponse(
        String id,
        String title,
        DocumentType type,
        EditDateTime dateTime,
        List<Info> info,
        List<List<String>> groups,
        DocumentEditor editor,
        String content,

        Integer version
) {
}
