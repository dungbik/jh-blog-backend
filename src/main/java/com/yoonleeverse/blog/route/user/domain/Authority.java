package com.yoonleeverse.blog.route.user.domain;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    ROLE_ADMIN, ROLE_USERß;

    public String getAuthority() {
        return name();
    }
}
