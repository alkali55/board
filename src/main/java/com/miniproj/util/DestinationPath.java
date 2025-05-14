package com.miniproj.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.net.http.HttpRequest;

@Slf4j
public class DestinationPath {

    private static String destPath;
    // 스태틱 변수로 잡으면 여러명의 사용자가 동시 이용할 때 혼선이 생길 수 있을듯?
    // ex : destPath = ~ 구문부터 req.getSession() ~ 구문 사이에
    // 새로운 요청으로 destPath 가 바뀌면
    // 의도한 바와 다른 값이 저장될 수 있을듯

    public static void setDestPath(HttpServletRequest req) {

        String uri = req.getRequestURI();
        String queryString = req.getQueryString(); // ?제외 한 쿼리스트링

//        if (queryString == null) {
//            // 쿼리스트링이 없다
//            destPath = uri;
//        } else {
//            destPath = uri + "?" + queryString;
//        }

        destPath = queryString == null ? uri : uri + "?" + queryString;

        log.info("destPath = {}", destPath);
        log.info("queryString = {}", queryString);

        req.getSession().setAttribute("destPath", destPath);

    }
}
