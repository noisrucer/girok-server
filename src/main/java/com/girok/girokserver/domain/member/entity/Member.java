package com.girok.girokserver.domain.member.entity;

import com.girok.girokserver.global.baseentity.AuditBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity(name = "member")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Member extends AuditBase {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @NotBlank
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    @NotBlank
    private String password;

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}
