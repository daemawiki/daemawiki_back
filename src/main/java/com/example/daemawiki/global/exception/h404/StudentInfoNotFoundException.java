package com.example.daemawiki.global.exception.h404;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class StudentInfoNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new StudentInfoNotFoundException();

    private StudentInfoNotFoundException() {
        super(ErrorCode.STUDENT_INFO_NOT_FOUND);
    }

}
