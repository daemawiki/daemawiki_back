package com.example.daemawiki.global.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {

    AUTH_MAIL("MAIL_"),
    AUTH_CODE("CODE_");

    private final String key;
}
