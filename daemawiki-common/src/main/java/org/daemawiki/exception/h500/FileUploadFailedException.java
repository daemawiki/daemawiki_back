package org.daemawiki.exception.h500;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class FileUploadFailedException extends CustomException {
    public static final CustomException EXCEPTION = new FileUploadFailedException();

    private FileUploadFailedException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

}
