package com.example.daemawiki.domain.mail.repository;

import com.example.daemawiki.domain.mail.model.AuthCode;
import com.example.daemawiki.domain.mail.model.AuthMail;
import com.example.daemawiki.domain.mail.type.RedisKey;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class AuthCodeRepository {
    private final ReactiveRedisOperations<String, String> redisOperations;

    public AuthCodeRepository(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    private static final String AUTHCODE = RedisKey.AUTH_CODE.getKey();

    public Mono<AuthCode> findByMail(String mail) {
        return redisOperations.opsForValue().get(AUTHCODE + mail)
                .map(value -> AuthCode.builder()
                        .mail(mail)
                        .code(value).build());
    }

    public Mono<AuthCode> delete(AuthCode authCode) {
        return redisOperations.delete(AUTHCODE + authCode.getMail())
                .map(deleted -> {
                    if (deleted == 1) {
                        return authCode;
                    }
                    return null;
                });
    }

    public Mono<AuthCode> save(AuthCode authCode) {
        return redisOperations.opsForValue()
                .set(AUTHCODE + authCode.getMail(),
                        authCode.getCode(),
                        Duration.ofHours(3))
                .map(result -> {
                    if (result) {
                        return authCode;
                    }
                    return null;
                });
    }


}
