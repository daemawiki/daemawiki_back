package com.example.daemawiki.domain.document.model;

import com.example.daemawiki.domain.content.model.Contents;
import com.example.daemawiki.domain.document.model.type.DocumentType;
import com.example.daemawiki.global.datetime.model.EditDateTime;
import lombok.Data;

@Data
public class DocumentSearchResult {
    private String id;
    private String title;
    private DocumentType type;
    private EditDateTime dateTime;
    private Contents content;
}