package com.example.daemawiki.global.exception.h404;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class ContentNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new ContentNotFoundException();

    private ContentNotFoundException() {
        super(ErrorCode.CONTENT_NOT_FOUND);
    }

}
