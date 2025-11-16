package com.example.campusin.application.oauth.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

public class TokenValidFailedException extends BusinessException {

    public TokenValidFailedException() {
        super(ErrorCode.TOKEN_VALIDATION_FAILED);
    }

    public TokenValidFailedException(String message) {
        super(ErrorCode.TOKEN_VALIDATION_FAILED, message);
    }
}
