package org.daemawiki.exception.h409;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class EmailAlreadyExistsException extends CustomException {
    public static final CustomException EXCEPTION = new EmailAlreadyExistsException();

    private EmailAlreadyExistsException() {
        super(ErrorCode.ALREADY_EXISTS_EMAIL);
    }

}
