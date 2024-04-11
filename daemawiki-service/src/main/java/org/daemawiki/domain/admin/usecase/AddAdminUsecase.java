package org.daemawiki.domain.admin.usecase;

import reactor.core.publisher.Mono;

public interface AddAdminUsecase {
    Mono<Void> add(String email);

}
