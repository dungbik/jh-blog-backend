package com.yoonleeverse.blog.route.user.controller;

import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.dto.LoginResponseDTO;
import com.yoonleeverse.blog.route.user.dto.LoginUserDTO;
import com.yoonleeverse.blog.route.user.dto.RefreshRequestDTO;
import com.yoonleeverse.blog.route.user.service.UserService;
import com.yoonleeverse.blog.route.user.dto.LoginRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequest) {
        return userService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/logout")
    public boolean logout(@AuthenticationPrincipal User user) {
        return userService.logout(user);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public LoginUserDTO me(@AuthenticationPrincipal User user) {
        return userService.me(user);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/refresh")
    public LoginResponseDTO refresh(@RequestBody @Valid RefreshRequestDTO request) {
        return userService.refresh(request.getRefreshToken());
    }

}
