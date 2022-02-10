package com.yoonleeverse.blog.route.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private String authToken;

    private String refreshToken;

    private LoginUserDTO user;
}
