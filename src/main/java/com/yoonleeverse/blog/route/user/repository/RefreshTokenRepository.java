package com.yoonleeverse.blog.route.user.repository;

import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String token);

    Optional<RefreshToken> findByUser(User user);
}
