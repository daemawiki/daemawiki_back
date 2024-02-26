package com.example.daemawiki.domain.document.model.mapper;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.dto.response.SearchDocumentResponse;
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
                .info(document.getInfo())
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
                .numberOfUpdate(document.getVersion())
                .updatedDate(document.getDateTime().getUpdated())
                .build());
    }

    public Mono<SearchDocumentResponse> defaultDocumentToSearchDocumentResponse(DefaultDocument document) {
        return Mono.just(SearchDocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .type(document.getType())
                .dateTime(document.getDateTime())
                .content(document.getContent().substring(0, Math.min(document.getContent().length(), 100)))
                .build());
    }

}
