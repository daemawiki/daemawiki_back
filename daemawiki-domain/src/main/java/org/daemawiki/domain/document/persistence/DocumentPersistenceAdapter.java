package org.daemawiki.domain.document.persistence;

import org.daemawiki.domain.document.application.DeleteDocumentPort;
import org.daemawiki.domain.document.application.FindDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.exception.h404.DocumentNotFoundException;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DocumentPersistenceAdapter implements SaveDocumentPort, FindDocumentPort, DeleteDocumentPort {
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
    public Flux<DocumentSearchResult> searchDocument(String text, PagingInfo pagingInfo) {
        return documentRepository.findByTextContaining(text, pagingInfo.sortBy().isBlank() ? "dateTime.created" : pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<DefaultDocument> searchDocumentTitle(String text, PagingInfo pagingInfo) {
        return documentRepository.findByTitleContaining(text, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentContent(String text, PagingInfo pagingInfo) {
        return documentRepository.findByContentTextContaining(text, pagingInfo.sortBy(), pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<DefaultDocument> getDocumentTop10() {
        return documentRepository.findTop10ByOrderByVersionDesc();
    }

    public Flux<DefaultDocument> getDocumentMostRevision(PagingInfo pagingInfo) {
        return documentRepository.findAllByOrderByVersion(pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<DefaultDocument> getDocumentOrderByView(PagingInfo pagingInfo) {
        return documentRepository.findAllByOrderByView(pagingInfo.sortDirection(), pagingInfo.page() * pagingInfo.size(), pagingInfo.size());
    }

    @Override
    public Flux<DefaultDocument> getDocumentOrderByViewTop10() {
        return documentRepository.findTop10ByOrderByViewDesc();
    }

    @Override
    public Mono<DefaultDocument> save(DefaultDocument document) {
        return documentRepository.save(document);
    }

    @Override
    public Mono<DefaultDocument> increaseView(DefaultDocument document) {
        return documentRepository.increaseView(document);
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
