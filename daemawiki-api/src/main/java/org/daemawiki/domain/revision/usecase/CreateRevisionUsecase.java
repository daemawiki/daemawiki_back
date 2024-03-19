package org.daemawiki.domain.revision.usecase;

import org.daemawiki.domain.revision.dto.request.SaveRevisionHistoryRequest;
import reactor.core.publisher.Mono;

public interface CreateRevisionUsecase {
    Mono<Void> saveHistory(SaveRevisionHistoryRequest request);

}
