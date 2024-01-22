package com.example.daemawiki.global.exception;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class PasswordMismatchException extends CustomException {
    public static final CustomException EXCEPTION = new PasswordMismatchException();

    private PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }

}
