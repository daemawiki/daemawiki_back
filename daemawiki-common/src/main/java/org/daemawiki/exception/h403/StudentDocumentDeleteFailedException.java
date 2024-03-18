package org.daemawiki.exception.h403;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class StudentDocumentDeleteFailedException extends CustomException {
    public static final CustomException EXCEPTION = new StudentDocumentDeleteFailedException();

    private StudentDocumentDeleteFailedException() {
        super(ErrorCode.DOCUMENT_DELETE_FAILED);
    }

}
