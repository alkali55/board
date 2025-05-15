package com.miniproj.service;

import com.miniproj.domain.AutoLoginInfo;
import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;

public interface MemberService {

    void register(Member member);

    Member login(LoginDTO loginDTO);

    boolean saveAutoLoginInfo(AutoLoginInfo autoLoginInfo);

    Member checkAutoLogin(String savedCookieSesId);

    void clearAutoLoginInfo(String memberId);
}
