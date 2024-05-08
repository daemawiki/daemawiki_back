package org.daemawiki.domain.document_content.usecase;

import org.daemawiki.domain.document_content.dto.WriteContentRequest;
import reactor.core.publisher.Mono;

public interface WriteContentUsecase {
    Mono<Void> write(WriteContentRequest request, String documentId);
}
