package com.example.daemawiki.domain.user.repository;

import com.example.daemawiki.domain.user.model.User;
import com.example.daemawiki.domain.user.model.type.MajorType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmail(String email);

    Flux<User> findAllByGen(Integer gen);

    Flux<User> findAllByMajor(MajorType major);
}
