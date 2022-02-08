package com.yoonleeverse.blog.user.controller;

import com.yoonleeverse.blog.user.domain.User;
import com.yoonleeverse.blog.user.dto.LoginUserDTO;
import com.yoonleeverse.blog.user.service.UserService;
import com.yoonleeverse.blog.user.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public LoginUserDTO login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword(), response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public boolean logout(@AuthenticationPrincipal User user, HttpServletResponse response) {
        return userService.logout(user, response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public boolean me() {
        return true;
    }

}
