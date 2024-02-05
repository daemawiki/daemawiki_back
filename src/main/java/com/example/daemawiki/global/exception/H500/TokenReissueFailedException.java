package com.example.daemawiki.global.exception.H500;

import com.example.daemawiki.global.error.ErrorCode;
import com.example.daemawiki.global.error.exception.CustomException;

public class TokenReissueFailedException extends CustomException {
    public static final CustomException EXCEPTION = new TokenReissueFailedException();

    private TokenReissueFailedException() {
        super(ErrorCode.TOKEN_REISSUE_FAILED);
    }

}
