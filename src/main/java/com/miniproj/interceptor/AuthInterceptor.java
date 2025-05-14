package com.miniproj.interceptor;

import com.miniproj.domain.HBoardDeatilInfo;
import com.miniproj.domain.Member;
import com.miniproj.service.BoardService;
import com.miniproj.util.DestinationPath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final BoardService boardService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("======================Auth preHandle() 호출======================");
        // 목적지 저장
        DestinationPath.setDestPath(request);

        // 로그인 여부 검사
        HttpSession session = request.getSession();
        Member loginMember = (Member)session.getAttribute("loginMember");

        if (loginMember == null) {
            log.info("로그인하지 않은 사용자 -> 로그인 페이지로 이동......");
            response.sendRedirect("/member/login");
            return false;
        }

        // 수정/삭제 요청인 경우 로그인멤버 == 작성자 같은지 확인
        String uri = request.getRequestURI();
        if (uri.contains("modify") || uri.contains("remove")) {
            int boardNo = Integer.parseInt(request.getParameter("boardNo"));
            List<HBoardDeatilInfo> detailList = boardService.viewBoardDetailInfoByNo(boardNo);
            String writer = detailList.get(0).getWriter().getMemberId();

            if(loginMember.getMemberId().equals(writer)){ // 본인글 수정
                return true;
            } else {
                // 본인글 수정이 아닌 경우 -> detail (상세)페이지로 보냄
                response.sendRedirect("/board/detail?status=authFail&boardNo=" + boardNo);
                return false;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("======================Auth postHandle() 호출======================");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("======================Auth afterCompletion() 호출======================");
    }
}
