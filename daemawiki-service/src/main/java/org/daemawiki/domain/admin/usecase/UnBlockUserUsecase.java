package org.daemawiki.domain.admin.usecase;

import reactor.core.publisher.Mono;

public interface UnBlockUserUsecase {
    Mono<Void> unblock(String userId);

}
