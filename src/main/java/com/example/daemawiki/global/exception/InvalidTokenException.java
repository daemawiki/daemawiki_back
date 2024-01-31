package com.example.daemawiki.global.exception;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class InvalidTokenException extends CustomException {
    public static final CustomException EXCEPTION = new InvalidTokenException();

    private InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }

}
