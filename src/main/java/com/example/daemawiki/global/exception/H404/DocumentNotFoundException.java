package com.example.daemawiki.global.exception.H404;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class DocumentNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new DocumentNotFoundException();

    private DocumentNotFoundException() {
        super(ErrorCode.DOCUMENT_NOT_FOUND);
    }

}
