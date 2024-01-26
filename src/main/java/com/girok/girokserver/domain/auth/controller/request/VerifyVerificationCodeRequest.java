package com.girok.girokserver.domain.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerifyVerificationCodeRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email address format")
    @Schema(example = "jason9792@gmail.com")
    private String email;

    @NotBlank(message = "Verification code cannot be empty")
    @Schema(example = "JS8FD2")
    private String verificationCode;

}
