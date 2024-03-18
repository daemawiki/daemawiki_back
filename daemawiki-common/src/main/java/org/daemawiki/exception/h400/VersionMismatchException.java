package org.daemawiki.exception.h400;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class VersionMismatchException extends CustomException {
    public static final CustomException EXCEPTION = new VersionMismatchException();

    private VersionMismatchException() {
        super(ErrorCode.VERSION_MISMATCH);
    }

}
