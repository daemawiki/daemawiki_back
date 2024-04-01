package org.daemawiki.domain.content.usecase;

import org.daemawiki.domain.content.dto.WriteContentRequest;
import reactor.core.publisher.Mono;

public interface WriteContentUsecase {
    Mono<Void> write(WriteContentRequest request, String documentId);
}
