package com.example.daemawiki.global.exception;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class EmailAlreadyExistsException extends CustomException {
    public static final CustomException EXCEPTION = new EmailAlreadyExistsException();

    private EmailAlreadyExistsException() {
        super(ErrorCode.ALREADY_EXISTS_EMAIL);
    }

}
