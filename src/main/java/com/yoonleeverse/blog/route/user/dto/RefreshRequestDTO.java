package com.yoonleeverse.blog.route.user.dto;
import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshRequestDTO {
    @NotNull
    private String refreshToken;
}
