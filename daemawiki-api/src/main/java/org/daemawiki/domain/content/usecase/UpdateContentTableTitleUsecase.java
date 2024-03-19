package org.daemawiki.domain.content.usecase;

import org.daemawiki.domain.content.dto.EditContentTableTitleRequest;
import reactor.core.publisher.Mono;

public interface UpdateContentTableTitleUsecase {
    Mono<Void> update(EditContentTableTitleRequest request, String documentId);

}
