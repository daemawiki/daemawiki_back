package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class UserNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new UserNotFoundException();

    private UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

}
