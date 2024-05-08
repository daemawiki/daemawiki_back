package org.daemawiki.domain.document.dto.response;

import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.document_content.model.Content;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.type.DocumentType;
import org.daemawiki.domain.document_editor.model.DocumentEditor;
import org.daemawiki.domain.document_info.model.Info;
import lombok.Builder;

import java.util.List;

@Builder
public record GetDocumentResponse(
        String id,
        String title,
        DocumentType type,
        EditDateTime dateTime,
        Info info,
        List<List<String>> groups,
        DocumentEditor editor,
        List<Content> content,
        Long view,
        Long version
) {

    public static GetDocumentResponse of(DefaultDocument document) {
        return GetDocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .type(document.getType())
                .dateTime(document.getDateTime())
                .info(document.getInfo())
                .groups(document.getGroups())
                .editor(document.getEditor())
                .content(document.getContents())
                .view(document.getView())
                .version(document.getVersion())
                .build();
    }

}
