package com.example.campusin.domain.token;
/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.application.oauth.CustomUserDetailsService;
import com.example.campusin.common.exception.TokenValidFailedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class AuthTokenProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "role";
    private final CustomUserDetailsService customUserDetailsService;

    public AuthTokenProvider(String secret, CustomUserDetailsService customUserDetailsService) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.customUserDetailsService = customUserDetailsService;
    }

    public AuthToken createAuthToken(String id, Date expiry) {
        return new AuthToken(id, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        return new AuthToken(id, role, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }


    // 참고 https://devjem.tistory.com/70
    public Authentication getAuthentication(AuthToken authToken) {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authToken.getTokenClaims().getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, authToken, userDetails.getAuthorities());

//        if(authToken.validate()) {
//
//            Claims claims = authToken.getTokenClaims();
//            Collection<? extends GrantedAuthority> authorities =
//                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
//                            .map(SimpleGrantedAuthority::new)
//                            .collect(Collectors.toList());
//
//            log.debug("claims subject := [{}]", claims.getSubject());
//            User principal = new User(claims.getSubject(), "", authorities);
//
//            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
//        } else {
//            throw new TokenValidFailedException();
//        }
    }

}
