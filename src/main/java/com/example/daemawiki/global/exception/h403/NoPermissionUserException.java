package com.example.daemawiki.global.exception.h403;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class NoPermissionUserException extends CustomException {
    public static final CustomException EXCEPTION = new NoPermissionUserException();

    private NoPermissionUserException() {
        super(ErrorCode.NO_PERMISSION_USER);
    }

}
