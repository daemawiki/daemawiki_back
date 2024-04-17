package org.daemawiki.domain.user.application;

import org.daemawiki.domain.user.model.User;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindUserPort {
    Mono<User> currentUser();
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
    Flux<User> findAllByGenAndMajorAndClub(Integer gen, String major, String club, String orderBy, String sort, Pageable pageable);

}
