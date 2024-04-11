package org.daemawiki.domain.admin.application;

import reactor.core.publisher.Mono;

public interface DeleteAdminAccountPort {
    Mono<Void> deleteByUserId(String userId);
    Mono<Void> deleteByEmail(String email);

}
