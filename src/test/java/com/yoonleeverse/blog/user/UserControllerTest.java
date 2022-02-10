package com.yoonleeverse.blog.user;

import com.yoonleeverse.blog.route.user.domain.Authority;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.dto.LoginRequestDTO;
import com.yoonleeverse.blog.route.user.dto.LoginResponseDTO;
import com.yoonleeverse.blog.route.user.repository.RefreshTokenRepository;
import com.yoonleeverse.blog.route.user.repository.UserRepository;
import com.yoonleeverse.blog.route.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.lang.String.format;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @LocalServerPort
    int port;

    public URI uri(String path) {
        try {
            return new URI(format("http://localhost:%d%s", port, path));
        }catch(Exception ex){
            throw new IllegalArgumentException();
        }
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void before() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User user = userService.save(User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("test"))
                .build());
        userService.addAuthority(user.getUserId(), Authority.ROLE_ADMIN);
    }

    @Test
    void test_1() {
        RestTemplate client = new RestTemplate();
        HttpEntity<LoginRequestDTO> body = new HttpEntity<>(
                new LoginRequestDTO("test@test.com", "test")
        );
        ResponseEntity<LoginResponseDTO> resp1 = client.exchange(uri("/user/login"), HttpMethod.POST, body, LoginResponseDTO.class);
        System.out.println(resp1.getStatusCodeValue() + " " + resp1.getBody());
    }
}