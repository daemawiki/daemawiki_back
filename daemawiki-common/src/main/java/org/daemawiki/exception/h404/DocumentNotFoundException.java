package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class DocumentNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new DocumentNotFoundException();

    private DocumentNotFoundException() {
        super(ErrorCode.DOCUMENT_NOT_FOUND);
    }

}
