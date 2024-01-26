package com.girok.girokserver.core.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenDto {
    private String accessToken;
    private String refreshToken;
}
