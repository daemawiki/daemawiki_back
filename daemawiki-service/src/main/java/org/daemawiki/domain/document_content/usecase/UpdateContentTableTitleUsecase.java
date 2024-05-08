package org.daemawiki.domain.document_content.usecase;

import org.daemawiki.domain.document_content.dto.EditContentTableTitleRequest;
import reactor.core.publisher.Mono;

public interface UpdateContentTableTitleUsecase {
    Mono<Void> update(EditContentTableTitleRequest request, String documentId);

}
