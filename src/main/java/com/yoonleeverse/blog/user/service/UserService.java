package com.yoonleeverse.blog.user.service;

import com.yoonleeverse.blog.security.JWTProvider;
import com.yoonleeverse.blog.security.SecurityProperties;
import com.yoonleeverse.blog.user.domain.RefreshToken;
import com.yoonleeverse.blog.user.dto.LoginUserDTO;
import com.yoonleeverse.blog.user.repository.RefreshTokenRepository;
import com.yoonleeverse.blog.user.repository.UserRepository;
import com.yoonleeverse.blog.user.domain.Authority;
import com.yoonleeverse.blog.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationProvider;
    private final JWTProvider jwtProvider;
    private final SecurityProperties securityProperties;

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<User> findUser(String email) {
        return userRepository.findUserByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void addAuthority(Long userId, Authority authority) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getAuthorities().size() == 0) {
                HashSet<Authority> authorities = new HashSet<>();
                authorities.add(authority);
                user.setAuthorities(authorities);
                save(user);
            }else if(!user.getAuthorities().contains(authority)) {
                HashSet<Authority> authorities = new HashSet<>();
                authorities.addAll(user.getAuthorities());
                authorities.add(authority);
                user.setAuthorities(authorities);
                save(user);
            }
        });
    }

    public void removeAuthority(Long userId, Authority authority) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getAuthorities() == null) return;

            if(user.getAuthorities().contains(authority)) {
                user.setAuthorities(
                        user.getAuthorities().stream().filter(auth->!auth.equals(authority))
                                .collect(Collectors.toSet())
                );
                save(user);
            }
        });
    }

    public LoginUserDTO login(String email, String password, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken credentials = new UsernamePasswordAuthenticationToken(email, password);
        try {
            SecurityContextHolder.getContext().setAuthentication(authenticationProvider.authenticate(credentials));

            User user = userRepository.findUserByEmail(email).orElseThrow(() -> new Exception());

            String authToken = jwtProvider.createAuthToken(user);
            String refreshToken = jwtProvider.createRefreshToken(user);

            refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);

            refreshTokenRepository.save(RefreshToken.builder()
                    .refreshToken(refreshToken)
                    .user(user)
                    .build());

            Cookie authCookie = new Cookie(securityProperties.getAuthTokenCookie(), authToken);
            authCookie.setHttpOnly(true);
            response.addCookie(authCookie);

            Cookie refreshCookie = new Cookie(securityProperties.getRefreshTokenCookie(), refreshToken);
            refreshCookie.setHttpOnly(true);
            response.addCookie(refreshCookie);

            return new LoginUserDTO(user.getEmail());

        } catch (Exception ex) {
            throw new BadCredentialsException(email, ex);
        }
    }

    public boolean logout(User user, HttpServletResponse response) {
        Cookie authToken = new Cookie(securityProperties.getAuthTokenCookie(), null);
        authToken.setMaxAge(0);
        authToken.setHttpOnly(true);
        response.addCookie(authToken);

        Cookie refreshToken = new Cookie(securityProperties.getRefreshTokenCookie(), null);
        refreshToken.setMaxAge(0);
        refreshToken.setHttpOnly(true);
        response.addCookie(refreshToken);

        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        return true;
    }

}