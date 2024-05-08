package org.daemawiki.domain.document_revision.usecase;

import org.daemawiki.domain.document_revision.dto.request.SaveRevisionHistoryDto;
import reactor.core.publisher.Mono;

public interface CreateRevisionUsecase {
    Mono<Void> saveHistory(SaveRevisionHistoryDto request);

}
