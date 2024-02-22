package com.example.daemawiki.global.exception.h400;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class VersionMismatchException extends CustomException {
    public static final CustomException EXCEPTION = new VersionMismatchException();

    private VersionMismatchException() {
        super(ErrorCode.VERSION_MISMATCH);
    }

}
