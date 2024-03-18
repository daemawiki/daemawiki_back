package org.daemawiki.exception.h403;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class UnVerifiedEmailException extends CustomException {
    public static final CustomException EXCEPTION = new UnVerifiedEmailException();

    private UnVerifiedEmailException() {
        super(ErrorCode.UNVERIFIED_EMAIL);
    }

}
