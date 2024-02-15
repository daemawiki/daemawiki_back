package com.example.daemawiki.global.exception.h500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class MailSendFailedException extends CustomException {
    public static final CustomException EXCEPTION = new MailSendFailedException();

    private MailSendFailedException() {
        super(ErrorCode.MAIL_CONFIRM_FAILED);
    }

}
