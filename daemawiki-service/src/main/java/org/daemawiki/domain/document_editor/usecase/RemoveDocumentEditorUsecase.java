package org.daemawiki.domain.document_editor.usecase;

import org.daemawiki.domain.document_editor.dto.DeleteEditorRequest;
import reactor.core.publisher.Mono;

public interface RemoveDocumentEditorUsecase {
    Mono<Void> remove(DeleteEditorRequest request, String documentId);

}
