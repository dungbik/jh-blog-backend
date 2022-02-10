package com.yoonleeverse.blog.route.user.resolver;

import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserQueryResolver implements GraphQLQueryResolver {

    private final UserService userService;

    public List<User> getAllUser() {
        return userService.getAllUser();
    }
}
