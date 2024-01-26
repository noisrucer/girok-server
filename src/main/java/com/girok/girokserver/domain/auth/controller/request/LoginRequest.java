package com.girok.girokserver.domain.auth.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Incorrect email address format")
    @Schema(example = "jason9792@gmail.com")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 7, max = 50, message = "Password must be 7 ~ 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%*!]).+$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one special character (@, #, $, %, *, !)")
    @Schema(example = "Cksfj112*")
    private String password;
}
