package com.girok.girokserver.domain.auth.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.security.jwt.JwtTokenProvider;
import com.girok.girokserver.core.security.jwt.dto.JwtTokenDto;
import com.girok.girokserver.domain.auth.entity.EmailVerification;
import com.girok.girokserver.domain.auth.repository.EmailVerificationRepository;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.repository.MemberRepository;
import com.girok.girokserver.global.enums.EmailVerificationType;
import com.girok.girokserver.infra.email.MailgunVerificationEmailManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import static com.girok.girokserver.core.exception.ErrorInfo.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final EmailVerificationRepository emailVerificationRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailgunVerificationEmailManager mailgunVerificationEmailManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${email_verification.expire_seconds}")
    private long verificationExpireDurationSeconds;

    /**
     * 1. 회원가입용 이메일 인증코드 전송
     */
    @Transactional
    public void sendVerificationCode(String email, EmailVerificationType type) {
        // Send verification code
        String verificationCode = generateVerificationCode();
        mailgunVerificationEmailManager.sendVerificationCode(email, verificationCode);

        // Upsert register verification entry (always make is_verified to False)
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findByEmailAndType(email, type);
        if (optionalEmailVerification.isPresent()) {
            EmailVerification emailVerification = optionalEmailVerification.get();

            emailVerification.updateVerificationCode(verificationCode);
            emailVerification.unverify();
            emailVerification.renewExpirationTimeFromNow(verificationExpireDurationSeconds);
        } else {
            EmailVerification newEmailVerification = EmailVerification.builder()
                    .email(email)
                    .verificationCode(verificationCode)
                    .isVerified(false)
                    .expirationTime(LocalDateTime.now().plusSeconds(verificationExpireDurationSeconds))
                    .type(type)
                    .build();

            emailVerificationRepository.save(newEmailVerification);
        }
    }

    /**
     * 2. 회원가입용 이메일 인증코드 인증
     */
    @Transactional
    public void verifyVerificationCode(String email, String verificationCode, EmailVerificationType type) {
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findByEmailAndType(email, type);
        if (optionalEmailVerification.isEmpty()) {
            throw new CustomException(EMAIL_VERIFICATION_NOT_FOUND);
        }

        EmailVerification emailVerification = optionalEmailVerification.get();

        if (emailVerification.isVerified()) {
            throw new CustomException(EMAIL_ALREADY_VERIFIED);
        }

        if (!emailVerification.getVerificationCode().equals(verificationCode)) {
            throw new CustomException(INVALID_VERIFICATION_CODE);
        }

        if (emailVerification.isExpired()) {
            throw new CustomException(VERIFICATION_CODE_EXPIRED);
        }

        emailVerification.verify();
    }

    /**
     * 3. 이메일 인증 완료 되었는지 확인
     */
    public void checkValidVerification(String email, String verificationCode, EmailVerificationType type) {
        Optional<EmailVerification> optionalEmailVerification = emailVerificationRepository.findByEmailAndType(email, type);
        if (optionalEmailVerification.isEmpty()) {
            throw new CustomException(EMAIL_VERIFICATION_NOT_FOUND);
        }

        EmailVerification emailVerification = optionalEmailVerification.get();

        if (!emailVerification.isVerified()) {
            throw new CustomException(EMAIL_NOT_VERIFIED);
        }

        if (!emailVerification.isValidVerificationCode(verificationCode)) {
            throw new CustomException(INVALID_VERIFICATION_CODE);
        }
    }

    public JwtTokenDto login(String email, String password) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new CustomException(MEMBER_NOT_EXIST);
        }
        Member member = optionalMember.get();

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(INVALID_PASSWORD);
        }

        return jwtTokenProvider.generateToken(member.getId());
    }

    @Transactional
    public void deleteResetPasswordVerification(String email) {
        emailVerificationRepository.deleteByEmail(email);
    }

    private String generateVerificationCode() {
        int tokenLen = 6;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new SecureRandom();
        StringBuilder verificationCode = new StringBuilder(tokenLen);
        for (int i = 0; i < tokenLen; i++) {
            verificationCode.append(
                    chars.charAt(random.nextInt(chars.length()))
            );
        }
        return verificationCode.toString();
    }

}
