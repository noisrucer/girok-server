package com.girok.girokserver.domain.auth.controller;

import com.girok.girokserver.core.security.jwt.dto.JwtTokenDto;
import com.girok.girokserver.domain.auth.controller.request.*;
import com.girok.girokserver.domain.auth.controller.response.CheckEmailRegisteredResponse;
import com.girok.girokserver.domain.auth.controller.response.LoginResponse;
import com.girok.girokserver.domain.auth.facade.AuthFacade;
import com.girok.girokserver.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Authentication Process API")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthFacade authFacade;

    @GetMapping("/health-check")
    @ResponseStatus(HttpStatus.OK)
    public String healthCheck() {
        return "I'm doing fine";
    }

    @PostMapping("/auth/verification-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Send email verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "**Bad Request**: \n" +
                    "- `MEMBER_ALREADY_EXIST`: Member with the given email already exists."
            ),
    })
    public void sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        authFacade.sendVerificationCode(request.getEmail());
    }

    @PostMapping("/auth/verification-code/check")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Verify email")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                    "- `MEMBER_ALREADY_EXIST`: Member with the given email already exists.\n" +
                    "- `EMAIL_VERIFICATION_NOT_FOUND`: Email verification with the given email is not found.\n" +
                    "- `EMAIL_ALREADY_VERIFIED`: Email address is already verified.\n" +
                    "- `INVALID_VERIFICATION_CODE`: Invalid verification code.\n" +
                    "- `VERIFICATION_CODE_EXPIRED`: Verification code is expired"
            ),
    })
    public void verifyVerificationCode(@Valid @RequestBody VerifyVerificationCodeRequest request) {
        authFacade.verifyVerificationCode(request.getEmail(), request.getVerificationCode());
    }

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registration")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "**Bad Request**:\n" +
                    "- `MEMBER_ALREADY_EXIST`: Member with the given email already exists.\n" +
                    "- `EMAIL_VERIFICATION_NOT_FOUND`: Email verification with the given email is not found.\n" +
                    "- `EMAIL_NOT_VERIFIED`: Email is not verified.\n" +
                    "- `INVALID_VERIFICATION_CODE`: Invalid verification code."
            ),
    })
    public void signUp(@Valid @RequestBody SignUpRequest request) {
        authFacade.signUp(request.getEmail(), request.getPassword(), request.getVerificationCode());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "**Bad Request**: \n" +
                    "- `MEMBER_NOT_EXIT`: Member with the given email does not exist.\n" +
                    "- `INVALID_PASSWORD`: Password is invalid."
            ),
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        JwtTokenDto jwtTokenDto = authFacade.login(request.getEmail(), request.getPassword());

        Cookie refreshTokenCookie = new Cookie("refreshToken", jwtTokenDto.getRefreshToken());
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok().body(new LoginResponse(jwtTokenDto.getAccessToken()));
    }

    @PostMapping("/auth/password-reset/code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Send reset password email verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "**Bad Request**: \n" +
                    "- `MEMBER_NOT_EXIST`: Member with the given email does not exist."
            ),
    })
    public void sendResetPasswordVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        authFacade.sendResetPasswordVerificationCode(request.getEmail());
    }

    @PostMapping("/auth/password-reset/verify-code")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Verify reset password verification code")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Bad Request: \n" +
                    "- `MEMBER_NOT_EXIST`: Member with the given email does not exist.\n" +
                    "- `EMAIL_VERIFICATION_NOT_FOUND`: Email verification with the given email is not found.\n" +
                    "- `EMAIL_ALREADY_VERIFIED`: Email address is already verified.\n" +
                    "- `INVALID_VERIFICATION_CODE`: Invalid verification code.\n" +
                    "- `VERIFICATION_CODE_EXPIRED`: Verification code is expired"
            ),
    })
    public void verifyResetPasswordVerificationCode(@Valid @RequestBody VerifyVerificationCodeRequest request) {
        authFacade.verifyResetPasswordVerificationCode(request.getEmail(), request.getVerificationCode());
    }

    @PatchMapping("/auth/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Reset password")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "**Bad Request**:\n" +
                    "- `MEMBER_NOT_EXIST`: Member with the given email does not exist.\n" +
                    "- `EMAIL_VERIFICATION_NOT_FOUND`: Email verification with the given email is not found.\n" +
                    "- `EMAIL_NOT_VERIFIED`: Email is not verified.\n" +
                    "- `INVALID_VERIFICATION_CODE`: Invalid verification code."
            ),
    })
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authFacade.resetPassword(request.getEmail(), request.getNewPassword(), request.getVerificationCode());
    }

    @GetMapping("/auth/email/registered")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Check whether the given email is registered")
    public ResponseEntity<CheckEmailRegisteredResponse> checkEmailRegistered(@Email @RequestParam("email") String email) {
        boolean isRegistered = authFacade.checkEmailRegistered(email);
        return ResponseEntity.ok().body(new CheckEmailRegisteredResponse(isRegistered));
    }

}
