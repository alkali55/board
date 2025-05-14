package com.miniproj.interceptor;

import com.miniproj.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // mapping되는 컨트롤러단의 메서드가 호출되기 이전에 request, response를 가로채서 동작

        log.info("======================== preHandle() 호출 ===============================");
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//        return false; // 해당 컨트롤러단의 메서드로 제어가 돌아가지 않는다.

        boolean result = false;

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

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 해당 interceptor의 preHandle, poshHandle의 전 과정이 끝난 후 view가 렌더링 된 후에 request와 reponse를 가로채서 동작
        log.info("======================== afterCompletion() 호출 ===============================");
    }
}
