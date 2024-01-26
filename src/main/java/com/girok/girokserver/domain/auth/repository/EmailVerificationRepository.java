package com.girok.girokserver.domain.auth.repository;

import com.girok.girokserver.domain.auth.entity.EmailVerification;
import com.girok.girokserver.global.enums.EmailVerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEmailAndType(String email, EmailVerificationType type);

    Boolean existsByEmailAndType(String email, EmailVerificationType type);

    void deleteByEmail(String email);

}
