package com.example.daemawiki.global.exception;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class UnVerifiedEmailException extends CustomException {
    public static final CustomException EXCEPTION = new UnVerifiedEmailException();

    private UnVerifiedEmailException() {
        super(ErrorCode.UNVERIFIED_EMAIL);
    }

}
