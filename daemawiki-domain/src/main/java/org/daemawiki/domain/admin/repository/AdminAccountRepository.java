package org.daemawiki.domain.admin.repository;

import org.daemawiki.domain.admin.model.Admin;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AdminAccountRepository extends ReactiveCrudRepository<Admin, String> {
    Mono<Admin> findByUserId(String userId);
    Mono<Admin> findByEmail(String email);
    Mono<Void> deleteByEmail(String email);
    Mono<Void> deleteByUserId(String userId);
    Mono<Boolean> existsByEmail(String email);

}
