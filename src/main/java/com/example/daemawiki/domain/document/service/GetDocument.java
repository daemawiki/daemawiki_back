package com.example.daemawiki.domain.document.service;

import com.example.daemawiki.domain.document.dto.response.GetDocumentResponse;
import com.example.daemawiki.domain.document.service.facade.DocumentFacade;
import com.example.daemawiki.global.dateTime.model.EditDateTime;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetDocument {
    private final DocumentFacade documentFacade;

    public GetDocument(DocumentFacade documentFacade) {
        this.documentFacade = documentFacade;
    }

    public Mono<GetDocumentResponse> execute(String id) {
        return documentFacade.findDocumentById(id)
                .flatMap(document -> getKor(document.getDateTime())
                        .map(editDateTime -> GetDocumentResponse.builder()
                                .title(document.getTitle())
                                .type(document.getType())
                                .dateTime(editDateTime)
                                .groups(document.getGroups())
                                .editor(document.getEditor())
                                .content(document.getContent())
                                .build()));
    }

    public Mono<EditDateTime> getKor(EditDateTime editDateTime) {
        return Mono.fromCallable(() -> {
            var createdTD = editDateTime.getCreated();
            var updatedTD = editDateTime.getUpdated();

            editDateTime.setCreated(createdTD.plusHours(9));
            editDateTime.setUpdated(updatedTD.plusHours(9));

            return editDateTime;
        });
    }

}
