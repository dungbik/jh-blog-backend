package com.yoonleeverse.blog.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yoonleeverse.blog.route.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JWTProvider {

    private final SecurityProperties securityProperties;

    private final UserDetailsService userDetailsService;

    private final Algorithm algorithm;

    public String createAuthToken(User user) {
        return createToken(user, securityProperties.getAuthTokenExpire());
    }

    public String createRefreshToken(User user) {
        return createToken(user, securityProperties.getRefreshTokenExpire());
    }

    private String createToken(User user, Long expireTime) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + expireTime);

        return JWT.create()
                .withIssuedAt(issuedAt)
                .withExpiresAt(expiredAt)
                .withSubject(user.getEmail())
                .sign(algorithm);
    }

    public boolean verifyToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String getEmail(String token) {
        return JWT.require(algorithm).build().verify(token).getSubject();
    }

    public Authentication getAuthentication(String token) {
        User userDetails = (User) userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

}
