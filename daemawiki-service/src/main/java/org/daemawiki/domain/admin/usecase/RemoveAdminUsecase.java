package org.daemawiki.domain.admin.usecase;

import reactor.core.publisher.Mono;

public interface RemoveAdminUsecase {
    Mono<Void> remove(String email);

}
