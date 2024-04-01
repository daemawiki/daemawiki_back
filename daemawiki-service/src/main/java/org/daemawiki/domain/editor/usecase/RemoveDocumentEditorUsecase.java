package org.daemawiki.domain.editor.usecase;

import org.daemawiki.domain.editor.dto.DeleteEditorRequest;
import reactor.core.publisher.Mono;

public interface RemoveDocumentEditorUsecase {
    Mono<Void> remove(DeleteEditorRequest request, String documentId);

}
