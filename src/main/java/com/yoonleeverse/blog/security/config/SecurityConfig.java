package com.yoonleeverse.blog.security.config;

import com.auth0.jwt.algorithms.Algorithm;
import com.yoonleeverse.blog.security.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public Algorithm jwtAlgorithm(SecurityProperties properties) {
        return Algorithm.HMAC256(properties.getSecretKey());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}
