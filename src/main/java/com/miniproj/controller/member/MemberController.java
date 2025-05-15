package com.miniproj.controller.member;

import com.miniproj.domain.LoginDTO;
import com.miniproj.domain.Member;
import com.miniproj.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/member")
@Slf4j
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginForm(){
        return "/member/login";
    }

//    @PostMapping("/login")
//    public String login(LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes){
//
//        log.info("loginDTO = {}", loginDTO);
//
//        Member loginMember = memberService.login(loginDTO);
//        log.info("loginMember = {}", loginMember);
//
//        if (loginMember != null){
//            log.info("로그인 성공..........");
//            session.setAttribute("loginMember", loginMember); // 세션에 저장
//            return "redirect:/";
//        } else {
//            log.info("로그인 실패..........");
//            redirectAttributes.addFlashAttribute("msg", "아이디 또는 비밀번호를 다시 확인해주세요");
//            return "redirect:/member/login";
//        }
//    }
    
    
    // 로그인 인터셉터 적용
    @PostMapping("/login")
    public void login(LoginDTO loginDTO, HttpSession session, RedirectAttributes redirectAttributes, Model model){

        log.info("loginDTO = {}", loginDTO);

        Member loginMember = memberService.login(loginDTO);
        log.info("loginMember = {}", loginMember);

        if (loginMember != null){
//            log.info("로그인 성공..........");
            model.addAttribute("loginMember", loginMember); // 로그인멤버 정보가 인터셉터로 전달되도록 함.

        } else {
            log.info("로그인 실패..........");

        }
    }

    @GetMapping("/logout")
    public String logoutMember(HttpSession session, HttpServletResponse response){
        Member loginMember = (Member)session.getAttribute("loginMember");

        if (loginMember != null){

            // 자동로그인 쿠키 삭제
            Cookie cookie = new Cookie("al", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // DB 자동 로그인 정보 삭제
            memberService.clearAutoLoginInfo(loginMember.getMemberId());

            session.removeAttribute("loginMember");
            session.removeAttribute("destPath");

            session.invalidate(); // 세션 무효화
        }

        
        return "redirect:/";
    }
}
