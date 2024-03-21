package org.daemawiki.domain.info.usecase;

import org.daemawiki.domain.info.dto.UpdateInfoRequest;
import reactor.core.publisher.Mono;

public interface UpdateDocumentInfoUsecase {
    Mono<Void> update(String documentId, UpdateInfoRequest request);

}
