package com.girok.girokserver.domain.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SendVerificationCodeRequest {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email address format")
    @Schema(example = "jason9792@gmail.com", description = "User email")
    private String email;
}
