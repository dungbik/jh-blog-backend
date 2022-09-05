package com.yoonleeverse.blog.route.user.service;

import com.yoonleeverse.blog.route.user.domain.Authority;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.dto.LoginResponseDTO;
import com.yoonleeverse.blog.route.user.dto.LoginUserDTO;
import com.yoonleeverse.blog.route.user.repository.UserRepository;
import com.yoonleeverse.blog.security.JWTProvider;
import com.yoonleeverse.blog.route.user.domain.RefreshToken;
import com.yoonleeverse.blog.route.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationProvider;
    private final JWTProvider jwtProvider;

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

    public LoginResponseDTO login(String email, String password) {
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

            return new LoginResponseDTO(authToken, refreshToken, new LoginUserDTO(user.getEmail()));
        } catch (Exception ex) {
            throw new BadCredentialsException(email, ex);
        }
    }

    public boolean logout(User user) {
        refreshTokenRepository.findByUser(user).ifPresent(refreshTokenRepository::delete);
        return true;
    }

    public LoginUserDTO me(User user) {
        return new LoginUserDTO(user.getEmail());
    }

    public LoginResponseDTO refresh(String oldRefreshToken) {
        log.debug("old {%s}", oldRefreshToken);

        RefreshToken oldToken = refreshTokenRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("refresh token not found : " + oldRefreshToken));

        User user = oldToken.getUser();
        String authToken = jwtProvider.createAuthToken(user);
        String refreshToken = jwtProvider.createRefreshToken(user);

        refreshTokenRepository.delete(oldToken);

        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .build());

        log.debug("old {%s}, new {%s}", oldRefreshToken, refreshToken);

        return new LoginResponseDTO(authToken, refreshToken, new LoginUserDTO(user.getEmail()));
    }

}