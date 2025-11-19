package com.example.campusin.support;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public final class SecurityUserFactory {

    private SecurityUserFactory() {
    }

    public static UserDetails securityUser(String username, String password, List<String> roles) {
        return User.withUsername(username)
                .password(password)
                .roles(roles.toArray(new String[0]))
                .build();
    }
}
