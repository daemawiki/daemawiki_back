package org.daemawiki.exception.h403;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class NoPermissionUserException extends CustomException {
    public static final CustomException EXCEPTION = new NoPermissionUserException();

    private NoPermissionUserException() {
        super(ErrorCode.NO_PERMISSION_USER);
    }

}
