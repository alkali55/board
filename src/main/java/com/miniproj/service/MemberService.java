package com.miniproj.service;

import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;

public interface MemberService {

    void register(Member member);

    Member login(LoginDTO loginDTO);
}
