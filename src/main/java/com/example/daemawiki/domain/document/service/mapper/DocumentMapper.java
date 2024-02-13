package com.example.daemawiki.domain.document.service.mapper;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.model.DefaultDocument;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DocumentMapper {

    public Mono<GetDocumentResponse> defaultDocumentToGetResponse(DefaultDocument document) {
        return Mono.justOrEmpty(GetDocumentResponse.builder()
                .title(document.getTitle())
                .type(document.getType())
                .dateTime(document.getDateTime())
                .groups(document.getGroups())
                .editor(document.getEditor())
                .content(document.getContent())
                .version(document.getVersion())
                .build());
    }

}