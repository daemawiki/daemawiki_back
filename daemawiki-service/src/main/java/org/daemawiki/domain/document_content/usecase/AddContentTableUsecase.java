package org.daemawiki.domain.document_content.usecase;

import org.daemawiki.domain.document_content.dto.AddContentRequest;
import reactor.core.publisher.Mono;

public interface AddContentTableUsecase {
    Mono<Void> add(AddContentRequest request, String documentId);

}
