package com.example.daemawiki.domain.user.repository;

import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.type.major.MajorType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);

    Flux<User> findAllByGenOrderByNameAsc(Integer gen);

    Flux<User> findAllByMajorOrderByNameAsc(MajorType major);
}
