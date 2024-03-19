package org.daemawiki.domain.user.application;

import org.daemawiki.domain.user.model.User;
import org.daemawiki.domain.user.model.type.major.MajorType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GetUserPort {
    Mono<User> currentUser();
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
    Flux<User> findAllByDetail_GenOrderByNameAsc(Integer gen);
    Flux<User> findAllByDetail_MajorOrderByNameAsc(MajorType major);
    Flux<User> findAllByDetail_ClubOrderByNameAsc(String club);

}
