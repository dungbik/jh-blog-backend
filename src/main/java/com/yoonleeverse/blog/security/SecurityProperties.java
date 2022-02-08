package com.yoonleeverse.blog.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private String secretKey;

    private long authTokenExpire;

    private long refreshTokenExpire;

    private String authTokenCookie;

    private String refreshTokenCookie;

}
