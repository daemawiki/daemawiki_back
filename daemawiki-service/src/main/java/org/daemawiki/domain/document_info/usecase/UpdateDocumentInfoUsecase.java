package org.daemawiki.domain.document_info.usecase;

import org.daemawiki.domain.document_info.dto.UpdateInfoRequest;
import reactor.core.publisher.Mono;

public interface UpdateDocumentInfoUsecase {
    Mono<Void> update(String documentId, UpdateInfoRequest request);

}
