package com.example.daemawiki.domain.document.service.mapper;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DocumentMapper {

    public Mono<GetDocumentResponse> defaultDocumentToGetResponse(DefaultDocument document) {
        return Mono.justOrEmpty(GetDocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .type(document.getType())
                .dateTime(document.getDateTime())
                .groups(document.getGroups())
                .editor(document.getEditor())
                .content(document.getContent())
                .version(document.getVersion())
                .build());
    }

    public Mono<SimpleDocumentResponse> defaultDocumentToSimpleDocumentResponse(DefaultDocument document) {
        return Mono.justOrEmpty(SimpleDocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .numberOfUpdate(document.getVersion() + 1)
                .updatedDate(document.getDateTime().getUpdated())
                .build());
    }

}
