package com.example.campusin.common.config.security;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.application.oauth.CustomUserDetailsService;
import com.example.campusin.domain.token.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    private final CustomUserDetailsService customUserDetailsService;

    public JwtConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public AuthTokenProvider jwtProvider() {
        return new AuthTokenProvider(secret, customUserDetailsService);
    }
}
