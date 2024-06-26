package org.daemawiki.domain.mail.repository;

import org.daemawiki.config.RedisKey;
import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.RedisConnectFailedException;
import org.springframework.data.redis.RedisConnectionFailureException;
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

    private static final String AUTH_CODE = RedisKey.AUTH_CODE.getKey();

    public Mono<AuthCode> findByMail(String mail) {
        return redisOperations.opsForValue().get(AUTH_CODE + mail)
                .map(value -> AuthCode.builder()
                        .mail(mail)
                        .code(value).build())
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION);
    }

    public Mono<Long> delete(AuthCode authCode) {
        return redisOperations.delete(AUTH_CODE + authCode.getMail())
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION);
    }

    public Mono<Boolean> save(AuthCode authCode) {
        return redisOperations.opsForValue()
                .set(AUTH_CODE + authCode.getMail(),
                        authCode.getCode(),
                        Duration.ofHours(3))
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION);
    }

}
