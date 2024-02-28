package com.example.daemawiki.global.exception.h403;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class NoEditPermissionUserException extends CustomException {
    public static final CustomException EXCEPTION = new NoEditPermissionUserException();

    private NoEditPermissionUserException() {
        super(ErrorCode.NO_EDIT_PERMISSION_USER);
    }

}
