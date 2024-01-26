package com.girok.girokserver.domain.member.service;

import com.girok.girokserver.core.exception.CustomException;
import com.girok.girokserver.core.exception.ErrorInfo;
import com.girok.girokserver.domain.member.entity.Member;
import com.girok.girokserver.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean isUserRegisteredByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void createMember(String email, String rawPassword) {
        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .build();

        memberRepository.save(member);
    }

    @Transactional
    public void updatePassword(String email, String newRawPassword) {
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        if (optionalMember.isEmpty()) {
            throw new CustomException(ErrorInfo.MEMBER_NOT_EXIST);
        }

        Member member = optionalMember.get();
        member.updatePassword(passwordEncoder.encode(newRawPassword));
    }

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
