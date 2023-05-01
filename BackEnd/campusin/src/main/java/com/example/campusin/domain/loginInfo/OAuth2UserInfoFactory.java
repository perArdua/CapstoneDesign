package com.example.campusin.domain.loginInfo;
/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.loginInfo.impl.FacebookOAuth2UserInfo;
import com.example.campusin.domain.loginInfo.impl.GoogleOAuth2UserInfo;
import com.example.campusin.domain.loginInfo.impl.KakaoOAuth2UserInfo;
import com.example.campusin.domain.loginInfo.impl.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK: return new FacebookOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
