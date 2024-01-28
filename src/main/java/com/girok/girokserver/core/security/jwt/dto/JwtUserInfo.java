package com.girok.girokserver.core.security.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtUserInfo {
    private final Long memberId;
}
