package com.example.daemawiki.domain.mail.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@RedisHash(timeToLive = 9000)
public class AuthMail { // 인증된 메일

    @Id
    private String mail;

}
