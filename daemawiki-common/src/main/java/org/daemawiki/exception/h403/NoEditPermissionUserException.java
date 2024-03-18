package org.daemawiki.exception.h403;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class NoEditPermissionUserException extends CustomException {
    public static final CustomException EXCEPTION = new NoEditPermissionUserException();

    private NoEditPermissionUserException() {
        super(ErrorCode.NO_EDIT_PERMISSION_USER);
    }

}
