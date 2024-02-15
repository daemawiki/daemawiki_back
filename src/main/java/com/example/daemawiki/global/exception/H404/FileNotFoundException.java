package com.example.daemawiki.global.exception.H404;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class FileNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new FileNotFoundException();

    private FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }

}
