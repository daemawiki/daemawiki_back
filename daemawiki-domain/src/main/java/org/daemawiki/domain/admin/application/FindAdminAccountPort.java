package org.daemawiki.domain.admin.application;

import org.daemawiki.domain.admin.model.Admin;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindAdminAccountPort {
    Mono<Admin> findByUserId(String userId);
    Mono<Admin> findByEmail(String email);
    Flux<Admin> findAll();
    Mono<Boolean> existsByEmail(String email);

}
