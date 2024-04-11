package org.daemawiki.domain.mail.repository;

import org.daemawiki.config.RedisKey;
import org.daemawiki.domain.mail.application.code.DeleteAuthCodePort;
import org.daemawiki.domain.mail.application.code.FindAuthCodePort;
import org.daemawiki.domain.mail.application.code.SaveAuthCodePort;
import org.daemawiki.domain.mail.model.AuthCode;
import org.daemawiki.exception.h500.ExecuteFailedException;
import org.daemawiki.exception.h500.RedisConnectFailedException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class AuthCodeRepository implements FindAuthCodePort, DeleteAuthCodePort, SaveAuthCodePort {
    private final ReactiveRedisOperations<String, String> redisOperations;

    public AuthCodeRepository(ReactiveRedisOperations<String, String> redisOperations) {
        this.redisOperations = redisOperations;
    }

    private static final String AUTHCODE = RedisKey.AUTH_CODE.getKey();

    @Override
    public Mono<AuthCode> findByMail(String mail) {
        return redisOperations.opsForValue().get(AUTHCODE + mail)
                .map(value -> AuthCode.builder()
                        .mail(mail)
                        .code(value).build())
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION);
    }

    @Override
    public Mono<Void> delete(AuthCode authCode) {
        return redisOperations.delete(AUTHCODE + authCode.getMail())
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION)
                .then();
    }

    @Override
    public Mono<Void> save(AuthCode authCode) {
        return redisOperations.opsForValue()
                .set(AUTHCODE + authCode.getMail(),
                        authCode.getCode(),
                        Duration.ofHours(3))
                .onErrorMap(e -> e instanceof RedisConnectionFailureException ? RedisConnectFailedException.EXCEPTION : ExecuteFailedException.EXCEPTION)
                .then();
    }

}
