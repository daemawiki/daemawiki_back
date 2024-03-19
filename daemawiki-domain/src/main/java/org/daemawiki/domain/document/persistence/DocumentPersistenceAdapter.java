package org.daemawiki.domain.document.persistence;

import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.application.DeleteDocumentPort;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.exception.h404.DocumentNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DocumentPersistenceAdapter implements SaveDocumentPort, GetDocumentPort, DeleteDocumentPort {
    private final DocumentRepository documentRepository;

    public DocumentPersistenceAdapter(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Mono<DefaultDocument> getDocumentById(String id) {
        return documentRepository.findById(id)
                .switchIfEmpty(Mono.error(DocumentNotFoundException.EXCEPTION));
    }

    @Override
    public Mono<DefaultDocument> getDocumentByRandom() {
        return documentRepository.findRandomDocument();
    }

    @Override
    public Flux<DocumentSearchResult> searchDocument(String text) {
        return documentRepository.findByTextContaining(text);
    }

    @Override
    public Flux<DefaultDocument> getDocumentTop10() {
        return documentRepository.findTop10ByOrderByVersionDesc();
    }

    @Override
    public Flux<DefaultDocument> getDocumentOrderByView() {
        return documentRepository.findAllByOrderByViewDesc();
    }

    @Override
    public Mono<DefaultDocument> save(DefaultDocument document) {
        return documentRepository.save(document);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return documentRepository.deleteById(id);
    }

    @Override
    public Mono<Void> delete(DefaultDocument document) {
        return documentRepository.delete(document);
    }

}
