package com.example.campusin.application.oauth;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.domain.user.Users;
import com.example.campusin.infra.user.UserRepository;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import com.example.campusin.common.exception.OAuthProviderMissMatchException;
import com.example.campusin.domain.loginInfo.OAuth2UserInfo;
import com.example.campusin.domain.loginInfo.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Users savedUsers = userRepository.findByUserId(userInfo.getId());

        if (savedUsers != null) {
            if (providerType != savedUsers.getProviderType()) {
                throw new OAuthProviderMissMatchException(
                        "Looks like you're signed up with " + providerType +
                        " account. Please use your " + savedUsers.getProviderType() + " account to login."
                );
            }
            updateUser(savedUsers, userInfo);
        } else {
            savedUsers = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUsers, user.getAttributes());
    }

    private Users createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        LocalDateTime now = LocalDateTime.now();
        Users users = new Users(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImageUrl(),
                providerType,
                RoleType.USER,
                now,
                now
        );

        return userRepository.saveAndFlush(users);
    }

    private Users updateUser(Users users, OAuth2UserInfo userInfo) {
        if (userInfo.getName() != null && !users.getUsername().equals(userInfo.getName())) {
            users.setUsername(userInfo.getName());
        }

        if (userInfo.getImageUrl() != null && !users.getProfileImageUrl().equals(userInfo.getImageUrl())) {
            users.setProfileImageUrl(userInfo.getImageUrl());
        }

        return users;
    }
}
