package com.example.daemawiki.domain.document.service.facade;

import com.example.daemawiki.domain.document.model.DefaultDocument;
import com.example.daemawiki.domain.document.repository.DocumentRepository;
import com.example.daemawiki.global.exception.H404.DocumentNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class DocumentFacade {
    private final DocumentRepository documentRepository;

    public DocumentFacade(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Mono<DefaultDocument> findDocumentByTitle(String title) {
        return documentRepository.findByTitle(title);
    }

    public Mono<DefaultDocument> findDocumentByTitleNotNull(String title) {
        return documentRepository.findByTitle(title)
                .switchIfEmpty(Mono.error(DocumentNotFoundException.EXCEPTION));
    }

    public Mono<DefaultDocument> findDocumentById(String id) {
        return documentRepository.findById(id)
                .switchIfEmpty(Mono.error(DocumentNotFoundException.EXCEPTION));
    }

}
