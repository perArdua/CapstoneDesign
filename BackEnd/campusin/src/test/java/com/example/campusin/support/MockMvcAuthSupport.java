package com.example.campusin.support;

import com.example.campusin.domain.oauth.ProviderType;
import com.example.campusin.domain.oauth.RoleType;
import com.example.campusin.domain.oauth.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

/**
 * 단위 테스트에서 SecurityContext에 인증 정보를 심기 위한 헬퍼.
 */
public final class MockMvcAuthSupport {

    private MockMvcAuthSupport() {
    }

    public static RequestPostProcessor authenticatedUser(long userId) {
        return authenticatedUser(buildPrincipal(userId));
    }

    public static RequestPostProcessor authenticatedUser(UserPrincipal principal) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        return request -> {
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
            return request;
        };
    }

    public static UserPrincipal buildPrincipal(long userId) {
        return new UserPrincipal(
                "login-" + userId,
                "password",
                userId,
                ProviderType.LOCAL,
                RoleType.USER,
                List.of(new SimpleGrantedAuthority(RoleType.USER.getCode()))
        );
    }
}
