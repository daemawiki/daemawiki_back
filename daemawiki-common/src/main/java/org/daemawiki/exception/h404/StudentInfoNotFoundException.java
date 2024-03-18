package org.daemawiki.exception.h404;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class StudentInfoNotFoundException extends CustomException {
    public static final CustomException EXCEPTION = new StudentInfoNotFoundException();

    private StudentInfoNotFoundException() {
        super(ErrorCode.STUDENT_INFO_NOT_FOUND);
    }

}
