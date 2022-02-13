package com.yoonleeverse.blog.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yoonleeverse.blog.route.user.domain.Authority;
import com.yoonleeverse.blog.route.user.domain.User;
import com.yoonleeverse.blog.route.user.dto.LoginRequestDTO;
import com.yoonleeverse.blog.route.user.dto.LoginResponseDTO;
import com.yoonleeverse.blog.route.user.dto.LoginUserDTO;
import com.yoonleeverse.blog.route.user.dto.RefreshRequestDTO;
import com.yoonleeverse.blog.route.user.repository.RefreshTokenRepository;
import com.yoonleeverse.blog.route.user.repository.UserRepository;
import com.yoonleeverse.blog.route.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static java.lang.String.format;
import static java.lang.Thread.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {


    @LocalServerPort
    int port;

    public URI uri(String path) {
        try {
            return new URI(format("http://localhost:%d%s", port, path));
        } catch(Exception ex) {
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

    @Autowired
    private ObjectMapper objectMapper;

    private RestTemplate client;

    static String testEmail = "test@test.com";
    static String testPassword = "test";

    @BeforeEach
    void before() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User user = userService.save(User.builder()
                .email(testEmail)
                .password(passwordEncoder.encode(testPassword))
                .build());
        userService.addAuthority(user.getUserId(), Authority.ROLE_ADMIN);

        client = new RestTemplateBuilder()
                .messageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    ResponseEntity<LoginResponseDTO> tryLogin(String email, String password) {
        HttpEntity<LoginRequestDTO> body = new HttpEntity<>(
                new LoginRequestDTO(email, password)
        );
        return client.exchange(uri("/user/login"), HttpMethod.POST, body, LoginResponseDTO.class);
    }

    @Test
    @DisplayName("로그인")
    void login() {
        ResponseEntity<LoginResponseDTO> res = tryLogin(testEmail, testPassword);
        LoginResponseDTO result = res.getBody();

        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getAuthToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotNull();
        assertThat(result.getUser().getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("로그인 비밀번호 불일치")
    void loginFail() {
        assertThatThrownBy(() -> tryLogin(testEmail, "fail"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("Forbidden");
    }

    ResponseEntity<LoginUserDTO> tryMe(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        HttpEntity request = new HttpEntity<>(headers);
        return client.exchange(uri("/user/me"), HttpMethod.GET, request, LoginUserDTO.class);
    }

    @Test
    @DisplayName("정상 토큰 확인")
    void me() {
        LoginResponseDTO loginResult = tryLogin(testEmail, testPassword).getBody();
        assertThat(loginResult.getAuthToken()).isNotNull();

        ResponseEntity<LoginUserDTO> res = tryMe(loginResult.getAuthToken());
        LoginUserDTO result = res.getBody();

        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getEmail()).isEqualTo(testEmail);
    }

    @Test
    @DisplayName("만료 토큰 확인")
    void meFail() {
        assertThatThrownBy(() -> tryMe("fail"))
                .isInstanceOf(HttpClientErrorException.class)
                .hasMessageContaining("Forbidden");
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        LoginResponseDTO loginResult = tryLogin(testEmail, testPassword).getBody();
        assertThat(loginResult.getAuthToken()).isNotNull();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + loginResult.getAuthToken());

        HttpEntity request = new HttpEntity<>(headers);
        ResponseEntity<Boolean> res = client.exchange(uri("/user/logout"), HttpMethod.GET, request, Boolean.class);

        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(res.getBody()).isTrue();
    }

    @Test
    @DisplayName("토큰 갱신")
    void refresh() throws InterruptedException {
        LoginResponseDTO loginResult = tryLogin(testEmail, testPassword).getBody();
        assertThat(loginResult.getRefreshToken()).isNotNull();

        sleep(1000);

        HttpEntity<RefreshRequestDTO> request = new HttpEntity<>(
                new RefreshRequestDTO(loginResult.getRefreshToken())
        );
        ResponseEntity<LoginResponseDTO> res = client.exchange(uri("/user/refresh"), HttpMethod.POST, request, LoginResponseDTO.class);
        LoginResponseDTO result = res.getBody();

        assertThat(res.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getAuthToken()).isNotNull();
        assertThat(result.getRefreshToken()).isNotEqualTo(loginResult.getRefreshToken());
        assertThat(result.getUser().getEmail()).isEqualTo(testEmail);
    }


}