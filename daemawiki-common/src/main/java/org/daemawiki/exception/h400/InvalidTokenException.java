package org.daemawiki.exception.h400;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class InvalidTokenException extends CustomException {
    public static final CustomException EXCEPTION = new InvalidTokenException();

    private InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

}
