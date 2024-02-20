package com.example.daemawiki.global.exception.h500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class ExecuteFailedException extends CustomException {
    public static final CustomException EXCEPTION = new ExecuteFailedException();

    private ExecuteFailedException() {
        super(ErrorCode.EXECUTE_FAILED);
    }

}
