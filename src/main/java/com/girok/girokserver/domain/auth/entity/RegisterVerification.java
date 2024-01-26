package com.girok.girokserver.domain.auth.entity;

import com.girok.girokserver.global.baseentity.AuditBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "register_verification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
public class RegisterVerification extends AuditBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    @Size(max = 100)
    @NotBlank
    private String email;

    @Column(name = "verification_code", nullable = false)
    @Size(max = 30)
    @NotBlank
    private String verificationCode;

    @Column(name = "is_verified", nullable = false)
    @NotNull
    private Boolean isVerified;

    @Column(name = "expiration_time", nullable = false)
    @NotNull
    private LocalDateTime expirationTime;

    /**
     * 비즈니스 로직
     **/
    public void verify() {
        isVerified = true;
    }

    public void unverify() {
        isVerified = false;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }

    public boolean isValidVerificationCode(String verificationCode) {
        return this.verificationCode.equals(verificationCode);
    }

    public void updateVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public void renewExpirationTimeFromNow(long expireDurationSeconds) {
        this.expirationTime = LocalDateTime.now().plusSeconds(expireDurationSeconds);
    }
}
