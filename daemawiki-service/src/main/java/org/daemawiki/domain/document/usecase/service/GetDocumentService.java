package org.daemawiki.domain.document.usecase.service;

import org.bson.types.ObjectId;
import org.daemawiki.domain.document.application.GetDocumentPort;
import org.daemawiki.domain.document.application.SaveDocumentPort;
import org.daemawiki.domain.document.dto.response.GetDocumentResponse;
import org.daemawiki.domain.document.dto.response.GetMostViewDocumentResponse;
import org.daemawiki.domain.document.dto.response.SimpleDocumentResponse;
import org.daemawiki.domain.document.model.DefaultDocument;
import org.daemawiki.domain.document.model.DocumentSearchResult;
import org.daemawiki.domain.document.usecase.GetDocumentUsecase;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class GetDocumentService implements GetDocumentUsecase {
    private final GetDocumentPort getDocumentPort;
    private final SaveDocumentPort saveDocumentPort;

    public GetDocumentService(GetDocumentPort getDocumentPort, SaveDocumentPort saveDocumentPort) {
        this.getDocumentPort = getDocumentPort;
        this.saveDocumentPort = saveDocumentPort;
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentById(String id) {
        return getDocumentPort.getDocumentById(id)
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Mono<GetDocumentResponse> getDocumentByRandom() {
        return getDocumentPort.getDocumentByRandom()
                .flatMap(saveDocumentPort::increaseView)
                .map(GetDocumentResponse::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocument(String text, String lastDocumentId) {
        return getFilteredSearchDocuments(
                getDocumentPort.searchDocument(text),
                lastDocumentId
        );
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentTitle(String text, String lastDocumentId) {
        return getFilteredDocuments(
                getDocumentPort.searchDocumentTitle(text),
                lastDocumentId
        ).map(DocumentSearchResult::of);
    }

    @Override
    public Flux<DocumentSearchResult> searchDocumentContent(String text, String lastDocumentId) {
        return getFilteredSearchDocuments(
                getDocumentPort.searchDocumentContent(text),
                lastDocumentId
        );
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentMostRevisionTop10() {
        return getDocumentPort.getDocumentTop10()
                .map(SimpleDocumentResponse::of);
    }

    @Override
    public Flux<SimpleDocumentResponse> getDocumentsMostRevision(String lastDocumentId) {
        return getFilteredDocuments(
                getDocumentPort.getDocumentMostRevision(),
                lastDocumentId
        ).map(SimpleDocumentResponse::of);
    }

    @Override
    public Flux<GetMostViewDocumentResponse> getDocumentOrderByView(String lastDocumentId) {
        return getFilteredDocuments(
                getDocumentPort.getDocumentOrderByView(),
                lastDocumentId
        ).map(GetMostViewDocumentResponse::of);
    }

    @Override
    public Flux<GetMostViewDocumentResponse> getDocumentOrderByViewTop10() {
        return getDocumentPort.getDocumentOrderByViewTop10()
                .map(GetMostViewDocumentResponse::of);
    }

    private Flux<DefaultDocument> getFilteredDocuments(Flux<DefaultDocument> documents, String lastDocumentId) {
        return documents.filter(doc -> lastDocumentId.isBlank() ||
                        new ObjectId(doc.getId()).getTimestamp() > new ObjectId(lastDocumentId).getTimestamp())
                .take(20);
    }

    private Flux<DocumentSearchResult> getFilteredSearchDocuments(Flux<DocumentSearchResult> documents, String lastDocumentId) {
        return documents.filter(doc -> lastDocumentId.isBlank() ||
                        new ObjectId(doc.getId()).getTimestamp() > new ObjectId(lastDocumentId).getTimestamp())
                .take(20);
    }

}
