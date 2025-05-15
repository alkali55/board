package com.miniproj.interceptor;

import com.miniproj.domain.AutoLoginInfo;
import com.miniproj.domain.Member;
import com.miniproj.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberService memberService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request, response를 가로채서 동작

        log.info("======================== preHandle() 호출 ===============================");
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//        return false; // 해당 컨트롤러단의 메서드로 제어가 돌아가지 않는다.

        boolean result = false;
        HttpSession ses = request.getSession();

        if(handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            
            // 메서드 이름
            Method methodobj = handlerMethod.getMethod();
            log.info("Method: {}", methodobj);

            // 클래스 이름
            String controllerName = handlerMethod.getBeanType().getName();
            log.info("controllerName : {}", controllerName);
        }

        // GET방식 /member/login 요청은 통과시켜야 한다.
        String uri = request.getRequestURI();
        String method = request.getMethod();
        if(uri.equals("/member/login") && method.equalsIgnoreCase("GET")) {
            result = true;
        }

        if(uri.equals("/member/login") && method.equalsIgnoreCase("POST")) {
            result = true;
        }

        // 자동 로그인 유저 (get방식 요청에서만 검사해도 될듯..)
        // 쿠키 검사하여 자동로그인 쿠키가 존재여부
        Cookie autoLoginCookie = WebUtils.getCookie(request, "al");
        if(autoLoginCookie != null) { // 쿠키가 있다
            String savedCookieSesId = autoLoginCookie.getValue();


            // DB에서 자동로그인 체크한 유저를 확인하고, 자동로그인 시켜야 한다.
            Member autoLoginUser = memberService.checkAutoLogin(savedCookieSesId);

            log.info("autoLoginUser : {}", autoLoginUser);

            if(autoLoginUser != null) {
                // 자동로그인 처리
                ses.setAttribute("loginMember", autoLoginUser);
                String destPath = (String)ses.getAttribute("destPath");
                response.sendRedirect((destPath != null) ? destPath : "/");
                return false;

            }

        } else { // 쿠키가 없고 로그인하지 않은 경우 로그인페이지를 보여준다.
            if(ses.getAttribute("loginMember") != null) {
                result = true;
            }


        }


        return result; // 해당 컨트롤러단의 메서드로 제어가 돌아간다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // mapping되는 컨트롤러단의 메서드가 호출되어 실행된 후에, request와 response를 가로채서 동작
        log.info("======================== postHandle() 호출 ===============================");
        if (request.getMethod().equalsIgnoreCase("POST")) { // POST 방식으로 호출됐을 때만 실행되도록.
            Map<String, Object> model = modelAndView.getModel();
            Member loginMember = (Member)model.get("loginMember");
            log.info("loginMember: {}", loginMember);


            if(loginMember != null) {
                log.info("postHandle()......로그인 성공");
                // 로그인 성공 -> 로그인멤버정보를 세션에 저장 -> "/" 홈으로
                HttpSession ses = request.getSession();
                ses.setAttribute("loginMember", loginMember);

                // 만약 자동로그인을 체크한 유저라면...
                if(request.getParameter("remember") != null) {
                    log.info("자동 로그인 유저입니다.....");
                    saveAutoLoginInfo(request, response);
                }

                String tmp = (String)ses.getAttribute("destPath");
                log.info("목적지 : {}", tmp);

                if (tmp != null) {
                    response.sendRedirect(tmp);
                } else {
                    response.sendRedirect("/");
                }

            } else {
                log.info("postHandle()......로그인 실패");
                // 로그인 실패 -> "/member/login" 로그인폼으로
                response.sendRedirect("/member/login?status=fail");
            }
        }

    }

    private void saveAutoLoginInfo(HttpServletRequest request, HttpServletResponse response) {
        // 자동로그인을 체크한 유저의 컬럼에 세션값과 만료일을 DB저장
        // 자동 로그인 쿠키 생성
        String sesId = request.getSession().getId();
        String loginMemberId = ((Member)request.getSession().getAttribute("loginMember")).getMemberId();

        // 만료일 : 일주일
        Timestamp allimit1 = new Timestamp(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));
        Instant instant = allimit1.toInstant();
        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("GMT"));

        Instant now = Instant.now();
        ZonedDateTime gmtDateTime = now.atZone(ZoneId.of("GMT"));
        log.info("gmtDateTime = {}", gmtDateTime);
//        Timestamp utcAllimit = Timestamp.from(utcDateTime.toInstant());
//        log.info("utcAllimit.toString() : {}", utcAllimit.toString());

        log.info("localDateTime + 7일 : {}", LocalDateTime.now().plusDays(7));
        log.info("localDateTime + 60초 : {}", LocalDateTime.now().plusSeconds(60));

        if(memberService.saveAutoLoginInfo(new AutoLoginInfo(loginMemberId, sesId, LocalDateTime.now().plusDays(7)))){

            // 쿠키 저장
            Cookie autoLoginCookie = new Cookie("al", sesId);
            autoLoginCookie.setMaxAge(7 * 24 * 60 * 60);
            autoLoginCookie.setPath("/");
            response.addCookie(autoLoginCookie);

        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 해당 interceptor의 preHandle, poshHandle의 전 과정이 끝난 후 view가 렌더링 된 후에 request와 reponse를 가로채서 동작
        log.info("======================== afterCompletion() 호출 ===============================");
    }
}
