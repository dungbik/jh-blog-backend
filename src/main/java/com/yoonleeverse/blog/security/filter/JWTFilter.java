package com.yoonleeverse.blog.security.filter;

import com.yoonleeverse.blog.security.JWTProvider;
import com.yoonleeverse.blog.security.SecurityProperties;
import com.yoonleeverse.blog.user.domain.User;
import com.yoonleeverse.blog.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;

import static java.util.function.Predicate.not;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private final SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authToken = getToken(request, securityProperties.getAuthTokenCookie()).orElse(null);
        String refreshToken = getToken(request, securityProperties.getRefreshTokenCookie()).orElse(null);

        if (authToken != null && jwtProvider.verifyToken(authToken)) {
            SecurityContextHolder.getContext().setAuthentication(jwtProvider.getAuthentication(authToken));
        } else if (refreshToken != null && jwtProvider.verifyToken(refreshToken)) {
            Authentication authentication = jwtProvider.getAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Cookie cookie = new Cookie(securityProperties.getAuthTokenCookie(), jwtProvider.createAuthToken((User) authentication.getPrincipal()));
            cookie.setHttpOnly(true);
            response.addCookie(cookie);

            filterChain.doFilter(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Optional<String> getToken(HttpServletRequest request, String name) {
        return Optional
                .ofNullable(request.getHeader(name.toLowerCase(Locale.ROOT)))
                .filter(not(String::isEmpty));
    }
}
