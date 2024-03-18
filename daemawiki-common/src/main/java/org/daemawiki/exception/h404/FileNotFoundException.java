package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class FileNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new FileNotFoundException();

    private FileNotFoundException() {
        super(ErrorCode.FILE_NOT_FOUND);
    }

}
