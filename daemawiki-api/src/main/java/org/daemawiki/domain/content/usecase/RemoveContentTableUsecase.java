package org.daemawiki.domain.content.usecase;

import org.daemawiki.domain.content.dto.DeleteContentRequest;
import reactor.core.publisher.Mono;

public interface RemoveContentTableUsecase {
    Mono<Void> remove(DeleteContentRequest request, String documentId);

}
