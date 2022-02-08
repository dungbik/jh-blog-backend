package com.yoonleeverse.blog.user.repository;

import com.yoonleeverse.blog.user.domain.RefreshToken;
import com.yoonleeverse.blog.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
