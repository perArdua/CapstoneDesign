package com.example.campusin.common.filter;

/**
 * Created by kok8454@gmail.com on 2023-03-19
 * Github : http://github.com/perArdua
 */

import com.example.campusin.common.utils.HeaderUtil;
import com.example.campusin.domain.token.AuthToken;
import com.example.campusin.domain.token.AuthTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (isPermitAllPath(uri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);

        if (token.validate()) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPermitAllPath(String uri) {
        return uri.startsWith("/swagger")
                || uri.startsWith("/v3")
                || uri.startsWith("/webjars")
                || uri.equals("/swagger-ui.html")
                || uri.startsWith("/swagger-resources")
                || uri.startsWith("/api-docs")
                || uri.startsWith("/actuator");
    }
}
