package com.example.daemawiki.domain.mail.repository;

import com.example.daemawiki.domain.mail.model.AuthCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AuthCodeRepository extends ReactiveCrudRepository<AuthCode, String> {
    Mono<AuthCode> findByMailAndCode(String mail, String code);
}
