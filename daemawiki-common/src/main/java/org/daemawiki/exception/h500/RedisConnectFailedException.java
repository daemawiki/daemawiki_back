package org.daemawiki.exception.h500;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class RedisConnectFailedException extends CustomException {
    public static final CustomException EXCEPTION = new RedisConnectFailedException();

    private RedisConnectFailedException() {
        super(ErrorCode.REDIS_CONNECT_FAILED);
    }

}
