package org.daemawiki.domain.content.usecase;

import org.daemawiki.domain.content.dto.AddContentRequest;
import reactor.core.publisher.Mono;

public interface AddContentTableUsecase {
    Mono<Void> add(AddContentRequest request, String documentId);

}
