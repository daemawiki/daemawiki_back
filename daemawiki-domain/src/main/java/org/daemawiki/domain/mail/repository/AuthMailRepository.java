package org.daemawiki.domain.mail.repository;

import org.daemawiki.config.RedisKey;
import org.daemawiki.domain.mail.model.AuthMail;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.RedisConnectFailedException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class AuthMailRepository {

    private final ReactiveRedisOperations<String, String> redisOperations;

    public AuthMailRepository(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    private static final String AUTHMAIL = RedisKey.AUTH_MAIL.getKey();

    public Mono<Void> save(AuthMail authMail) {
        return redisOperations.opsForValue()
                .set(AUTHMAIL + authMail.getMail(),
                        authMail.getMail(),
                        Duration.ofHours(3))
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION)
                .then();
    }

    public Mono<Boolean> findByMail(String mail) {
        return redisOperations.opsForValue()
                .get(AUTHMAIL + mail)
                .hasElement()
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION);
    }

    public Mono<Void> delete(String mail) {
        return redisOperations.delete(AUTHMAIL + mail)
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION)
                .then();
    }

}
