package com.yoonleeverse.blog.user.resolver;

import com.yoonleeverse.blog.user.service.UserService;
import com.yoonleeverse.blog.user.domain.User;
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
