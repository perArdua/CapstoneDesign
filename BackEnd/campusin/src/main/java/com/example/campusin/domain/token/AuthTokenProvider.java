package com.example.campusin.domain.token;
/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.application.oauth.CustomUserDetailsService;
import com.example.campusin.application.oauth.exception.TokenValidFailedException;
import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public AuthToken createAuthToken(String id, String loginId, String roleType, String providerType, Date expiry) {
        return new AuthToken(id, loginId, roleType, providerType, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }


    public Authentication getAuthentication(AuthToken authToken) {

        if(authToken.validate()) {
            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            log.debug("claims subject := [{}]", claims.getSubject());
            UserPrincipal principal = new UserPrincipal(
                    claims.getSubject(),
                    "",
                    Long.parseLong(claims.get("userId", String.class)),
                    ProviderType.valueOf(claims.get("providerType", String.class)),
                    RoleType.of(claims.get(AUTHORITIES_KEY, String.class)),
                    (Collection<GrantedAuthority>) authorities
            );

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            throw new TokenValidFailedException();
        }
    }
}
