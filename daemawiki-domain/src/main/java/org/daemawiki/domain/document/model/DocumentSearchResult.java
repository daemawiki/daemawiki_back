package org.daemawiki.domain.document.model;

import lombok.Builder;
import lombok.Data;
import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.document.model.type.DocumentType;

@Builder
@Data
public class DocumentSearchResult {
    private String id;
    private String title;
    private DocumentType type;
    private EditDateTime dateTime;
    private Content contents;
    private Integer view;

    public static DocumentSearchResult of(DefaultDocument document) {
        return DocumentSearchResult.builder()
                .id(document.getId())
                .title(document.getTitle())
                .type(document.getType())
                .dateTime(document.getDateTime())
                .contents(document.getContents().get(0))
                .view(document.getView())
                .build();

    }

}