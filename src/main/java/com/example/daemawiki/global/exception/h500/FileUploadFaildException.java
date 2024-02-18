package com.example.daemawiki.global.exception.h500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class FileUploadFaildException extends CustomException {
    public static final CustomException EXCEPTION = new FileUploadFaildException();

    private FileUploadFaildException() {
        super(ErrorCode.FILE_UPLOAD_FAILED);
    }

}
