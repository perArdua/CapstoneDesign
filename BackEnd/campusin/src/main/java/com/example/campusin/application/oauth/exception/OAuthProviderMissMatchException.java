package com.example.campusin.application.oauth.exception;

import com.example.campusin.common.exception.core.BusinessException;
import com.example.campusin.common.exception.core.ErrorCode;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

public class OAuthProviderMissMatchException extends BusinessException {

    public OAuthProviderMissMatchException() {
        super(ErrorCode.OAUTH_PROVIDER_MISMATCH);
    }

    public OAuthProviderMissMatchException(String message) {
        super(ErrorCode.OAUTH_PROVIDER_MISMATCH, message);
    }
}
