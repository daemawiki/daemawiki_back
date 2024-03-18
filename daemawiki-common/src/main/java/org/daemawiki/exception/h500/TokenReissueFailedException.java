package org.daemawiki.exception.h500;

import org.daemawiki.error.ErrorCode;
import org.daemawiki.error.exception.CustomException;

public class TokenReissueFailedException extends CustomException {
    public static final CustomException EXCEPTION = new TokenReissueFailedException();

    private TokenReissueFailedException() {
        super(ErrorCode.TOKEN_REISSUE_FAILED);
    }

}
