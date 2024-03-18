package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class ContentNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new ContentNotFoundException();

    private ContentNotFoundException() {
        super(ErrorCode.CONTENT_NOT_FOUND);
    }

}
