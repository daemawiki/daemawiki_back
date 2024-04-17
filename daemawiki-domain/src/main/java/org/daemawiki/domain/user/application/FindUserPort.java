package org.daemawiki.domain.user.application;

import org.daemawiki.domain.user.dto.FindUserDto;
import org.daemawiki.domain.user.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FindUserPort {
    Mono<User> currentUser();
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
    Flux<User> findAllByGenAndMajorAndClub(FindUserDto request);

}
