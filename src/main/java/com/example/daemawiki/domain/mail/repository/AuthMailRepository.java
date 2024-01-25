package com.example.daemawiki.domain.mail.repository;

import com.example.daemawiki.domain.mail.model.AuthMail;
import com.example.daemawiki.domain.mail.type.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class AuthMailRepository {

    private final ReactiveRedisOperations<String, String> redisOperations;

    @Autowired
    public AuthMailRepository(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    private static final String AUTHMAIL = RedisKey.AUTH_MAIL.getKey();

    public Mono<Void> save(AuthMail authMail) {
        return redisOperations.opsForValue()
                .set(AUTHMAIL + authMail.getMail(),
                        authMail.getMail(),
                        Duration.ofHours(9))
                .then();
    }

}
