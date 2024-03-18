package org.daemawiki.exception.h500;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class MailSendFailedException extends CustomException {
    public static final CustomException EXCEPTION = new MailSendFailedException();

    private MailSendFailedException() {
        super(ErrorCode.MAIL_CONFIRM_FAILED);
    }

}
