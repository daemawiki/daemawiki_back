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
}