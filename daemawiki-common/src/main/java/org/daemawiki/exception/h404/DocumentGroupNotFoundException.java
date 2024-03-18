package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class DocumentGroupNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new DocumentGroupNotFoundException();

    private DocumentGroupNotFoundException() {
        super(ErrorCode.DOCUMENT_GROUP_NOT_FOUND);
    }

}
