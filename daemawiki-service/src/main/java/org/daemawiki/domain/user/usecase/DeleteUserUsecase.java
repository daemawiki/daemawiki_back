package org.daemawiki.domain.user.usecase;

import reactor.core.publisher.Mono;

public interface DeleteUserUsecase {
    Mono<Void> deleteCurrentUser();

}
