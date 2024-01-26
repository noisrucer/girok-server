package com.girok.girokserver.domain.auth.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginResponse {

    @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiZXhwIjoxNzA2MTc4MjQyfQ.k4sADSFA2j0h9UOUvrGDgZZWHXd9ujVhgqkRleiFFWQ")
    private String accessToken;
}
