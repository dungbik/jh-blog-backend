package com.yoonleeverse.blog.db;

import com.yoonleeverse.blog.route.user.domain.Authority;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.service.UserService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DBInit implements InitializingBean {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    public DBInit(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!userService.findUser("test@test.com").isPresent()) {
            User user = userService.save(User.builder()
                    .email("test@test.com")
                    .password(passwordEncoder.encode("test"))
                    .build());
            userService.addAuthority(user.getUserId(), Authority.ROLE_ADMIN);
        }


    }
}
