package org.daemawiki.domain.editor.usecase;

import org.daemawiki.domain.editor.dto.AddEditorRequest;
import reactor.core.publisher.Mono;

public interface AddDocumentEditorUsecase {
    Mono<Void> add(AddEditorRequest request, String documentId);

}
