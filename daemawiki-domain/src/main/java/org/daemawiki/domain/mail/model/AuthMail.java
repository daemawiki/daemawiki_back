package org.daemawiki.domain.mail.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@RedisHash(timeToLive = 180 * 60)
public class AuthMail {

    @Id
    private String mail;

}
