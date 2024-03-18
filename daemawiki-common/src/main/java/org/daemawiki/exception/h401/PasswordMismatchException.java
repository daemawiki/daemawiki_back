package org.daemawiki.exception.h401;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class PasswordMismatchException extends CustomException {
    public static final CustomException EXCEPTION = new PasswordMismatchException();

    private PasswordMismatchException() {
        super(ErrorCode.PASSWORD_MISMATCH);
    }

}
