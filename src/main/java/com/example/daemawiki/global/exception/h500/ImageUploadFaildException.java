package com.example.daemawiki.global.exception.h500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class ImageUploadFaildException extends CustomException {
    public static final CustomException EXCEPTION = new ImageUploadFaildException();

    private ImageUploadFaildException() {
        super(ErrorCode.IMAGE_UPLOAD_FAILED);
    }

}
