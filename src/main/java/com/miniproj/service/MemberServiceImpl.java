package com.miniproj.service;

import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;
import com.miniproj.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public void register(Member member) {
        String encryptedPwd = passwordEncoder.encode(member.getMemberPwd());
        member.setMemberPwd(encryptedPwd);
        memberMapper.insertMember(member);
    }

    @Override
    public Member login(LoginDTO loginDTO) {
        Member member = memberMapper.findById(loginDTO.getMemberId());
        if (member != null && passwordEncoder.matches(loginDTO.getMemberPwd(), member.getMemberPwd())) {
            log.info("member : {}", member);
            return member;
        }

        return null;
    }
}
