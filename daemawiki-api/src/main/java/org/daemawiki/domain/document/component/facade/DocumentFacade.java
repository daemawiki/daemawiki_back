package org.daemawiki.domain.document.component.facade;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.exception.h404.DocumentNotFoundException;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DocumentFacade {
    private final DocumentRepository documentRepository;

    public DocumentFacade(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Mono<DefaultDocument> findDocumentById(String id) {
        return documentRepository.findById(id)
                .switchIfEmpty(Mono.error(DocumentNotFoundException.EXCEPTION));
    }

    public Mono<DefaultDocument> findDocumentByRandom() {
        return documentRepository.findRandomDocument();
    }

    public Flux<DocumentSearchResult> searchDocument(String text) {
        return documentRepository.findByTextContaining(text);
    }

    public Flux<DefaultDocument> getDocumentOrderByVersion() {
        return documentRepository.findTop10ByOrderByVersionDesc();
    }

    public Mono<DefaultDocument> saveDocument(DefaultDocument document) {
        return documentRepository.save(document)
                .onErrorMap(e -> ExecuteFailedException.EXCEPTION);
    }

    public Flux<DefaultDocument> getDocumentOrderByView() {
        return documentRepository.findAllByOrderByViewDesc();
    }

}
