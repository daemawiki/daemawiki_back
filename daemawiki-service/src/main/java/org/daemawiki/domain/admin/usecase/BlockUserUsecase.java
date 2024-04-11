package org.daemawiki.domain.admin.usecase;

import reactor.core.publisher.Mono;

public interface BlockUserUsecase {
    Mono<Void> block(String userId);

}
