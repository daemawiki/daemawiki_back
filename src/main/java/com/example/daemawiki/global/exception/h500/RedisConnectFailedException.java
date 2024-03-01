package com.example.daemawiki.global.exception.h500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class RedisConnectFailedException extends CustomException {
    public static final CustomException EXCEPTION = new RedisConnectFailedException();

    private RedisConnectFailedException() {
        super(ErrorCode.REDIS_CONNECT_FAILED);
    }

}
