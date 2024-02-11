package com.girok.girokserver.domain.auth.facade;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.core.security.jwt.dto.JwtTokenDto;
import com.girok.girokserver.domain.auth.service.AuthService;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.service.MemberService;
import com.girok.girokserver.global.enums.EmailVerificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.girok.girokserver.core.exception.ErrorInfo.*;
import static com.girok.girokserver.global.enums.EmailVerificationType.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthFacade {

    private final AuthService authService;
    private final MemberService memberService;

    /**
     * Send verification code to the email.
     * 1) If the first request for the email, a new entry will be created.
     * 2) If the email is already verified, this request will invalidate the existing verification
     *
     * @param email Target email.
     */
    @Transactional
    public void sendVerificationCode(String email) {
        checkMemberNotRegisteredByEmail(email);
        authService.sendVerificationCode(email, REGISTRATION);
    }

    /**
     * Verify the email.
     *
     * @param email Email passed on /api/v1/auth/verification-code
     * @param verificationCode Verification code sent to the email on /api/v1/auth/verification-code
     */
    @Transactional
    public void verifyVerificationCode(String email, String verificationCode) {
        checkMemberNotRegisteredByEmail(email);
        authService.verifyVerificationCode(email, verificationCode, REGISTRATION);
    }

    @Transactional
    public void signUp(String email, String password, String verificationCode) {
        checkMemberNotRegisteredByEmail(email); // Ensure no duplicate user
        authService.checkValidVerification(email, verificationCode, REGISTRATION); // Ensure email and code are valid
        memberService.createMember(email, password); // Create new member
    }

    @Transactional
    public void sendResetPasswordVerificationCode(String email) {
        checkMemberRegisteredByEmail(email);
        authService.sendVerificationCode(email, RESET_PASSWORD);
    }

    @Transactional
    public void verifyResetPasswordVerificationCode(String email, String verificationCode) {
        checkMemberRegisteredByEmail(email);
        authService.verifyVerificationCode(email, verificationCode, RESET_PASSWORD);
    }

    @Transactional
    public void resetPassword(String email, String newPassword, String verificationCode) {
        checkMemberRegisteredByEmail(email);
        authService.checkValidVerification(email, verificationCode, RESET_PASSWORD);
        memberService.updatePassword(email, newPassword);
        authService.deleteResetPasswordVerification(email);
    }

    public JwtTokenDto login(String email, String password) {
        return authService.login(email, password);
    }

    public boolean checkEmailRegistered(String email) {
        return memberService.isUserRegisteredByEmail(email);
    }

    private void checkMemberNotRegisteredByEmail(String email) {
        if (memberService.isUserRegisteredByEmail(email)) {
            throw new CustomException(MEMBER_ALREADY_EXIST);
        }
    }

    private void checkMemberRegisteredByEmail(String email) {
        if (!memberService.isUserRegisteredByEmail(email)) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
    }

}
