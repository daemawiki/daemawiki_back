package org.daemawiki.exception.h500;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class ExecuteFailedException extends CustomException {
    public static final CustomException EXCEPTION = new ExecuteFailedException();

    private ExecuteFailedException() {
        super(ErrorCode.EXECUTE_FAILED);
    }

}
