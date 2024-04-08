package org.daemawiki.domain.document.model;

import lombok.Builder;
import org.daemawiki.datetime.model.EditDateTime;
import org.daemawiki.domain.content.model.Content;
import org.daemawiki.domain.document.model.type.DocumentType;
import lombok.Data;

@Builder
@Data
public class DocumentSearchResult {
    private String id;
    private String title;
    private DocumentType type;
    private EditDateTime dateTime;
    private Content content;
    private Integer view;
}