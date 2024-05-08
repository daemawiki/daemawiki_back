package org.daemawiki.domain.document.adapter;

import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.port.DeleteDocumentPort;
import org.daemawiki.domain.document.port.FindDocumentPort;
import org.daemawiki.domain.document.port.SaveDocumentPort;
import org.daemawiki.domain.document.repository.DocumentRepository;
import org.daemawiki.utils.PagingInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DocumentAdapter implements SaveDocumentPort, FindDocumentPort, DeleteDocumentPort {
    private final DocumentRepository documentRepository;

    public DocumentAdapter(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Mono<DefaultDocument> findById(String id) {
        return documentRepository.findById(id);
    }

    @Override
    public Mono<DefaultDocument> findRandom() {
        return documentRepository.findRandomDocument();
    }

    @Override
    public Flux<DocumentSearchResult> search(String text, PagingInfo pagingInfo) {
        return documentRepository.findByTextContaining(
                text,
                pagingInfo.sortBy(),
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
        );
    }

    @Override
    public Flux<DefaultDocument> searchByTitle(String text, PagingInfo pagingInfo) {
        return documentRepository.findByTitleContaining(
                text,
                pagingInfo.sortBy(),
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size());
    }

    @Override
    public Flux<DocumentSearchResult> searchByContent(String text, PagingInfo pagingInfo) {
        return documentRepository.findByContentTextContaining(
                text,
                pagingInfo.sortBy(),
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
        );
    }

    public Flux<DefaultDocument> findAllSortByMostRevision(PagingInfo pagingInfo) {
        return documentRepository.findAllByOrderByVersion(
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
        );
    }

    @Override
    public Flux<DefaultDocument> findAllSortByView(PagingInfo pagingInfo) {
        return documentRepository.findAllByOrderByView(
                pagingInfo.sortDirection(),
                pagingInfo.page() * pagingInfo.size(),
                pagingInfo.size()
        );
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
