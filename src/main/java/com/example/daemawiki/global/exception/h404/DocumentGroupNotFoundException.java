package com.example.daemawiki.global.exception.h404;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class DocumentGroupNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new DocumentGroupNotFoundException();

    private DocumentGroupNotFoundException() {
        super(ErrorCode.DOCUMENT_GROUP_NOT_FOUND);
    }

}
