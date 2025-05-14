package com.miniproj.service;

import com.miniproj.domain.Member;
import com.miniproj.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class MemberServiceTests {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberMapper memberMapper;

    @Test
    public void testRegister(){

        Member member = Member.builder()
                .memberId("user02")
                .memberPwd("1234")
                .memberName("유저02")
                .email("user02@abc.com")
                .mobile("010-1111-1212")
                .gender("F")
                .build();

        memberService.register(member);

        Member member1 = memberMapper.findById(member.getMemberId());

        log.info("member1 = {}", member1);

    }

    @Test
    public void testFindById(){

        String tmpId = "user03";
        Member member = memberMapper.findById(tmpId);
        // 회원이 아니면 member = null;
        log.info("member = {}", member);
    }
}
