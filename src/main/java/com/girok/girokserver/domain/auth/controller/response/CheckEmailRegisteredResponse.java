package com.girok.girokserver.domain.auth.controller.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CheckEmailRegisteredResponse {

    @Schema(example = "false")
    private Boolean isRegistered;
}
