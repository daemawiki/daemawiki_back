package org.daemawiki.domain.document_editor.usecase;

import org.daemawiki.domain.document_editor.dto.AddEditorRequest;
import reactor.core.publisher.Mono;

public interface AddDocumentEditorUsecase {
    Mono<Void> add(AddEditorRequest request, String documentId);

}
