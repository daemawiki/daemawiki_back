package org.daemawiki.domain.document_content.usecase;

import org.daemawiki.domain.document_content.dto.DeleteContentRequest;
import reactor.core.publisher.Mono;

public interface RemoveContentTableUsecase {
    Mono<Void> remove(DeleteContentRequest request, String documentId);

}
