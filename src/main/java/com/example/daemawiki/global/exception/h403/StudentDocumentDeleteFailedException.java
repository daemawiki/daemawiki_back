package com.example.daemawiki.global.exception.h403;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class StudentDocumentDeleteFailedException extends CustomException {
    public static final CustomException EXCEPTION = new StudentDocumentDeleteFailedException();

    private StudentDocumentDeleteFailedException() {
        super(ErrorCode.DOCUMENT_DELETE_FAILED);
    }

}
