package com.miniproj.controller;

import com.miniproj.domain.PagingRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
public class HomeController {

    @ModelAttribute // 컨트롤러의 모든 메서드에 뷰로 model에 바인딩하여 보냄.
    public PagingRequestDTO addPagingInfo(@ModelAttribute PagingRequestDTO pagingRequestDTO){

        return pagingRequestDTO;
    }

    @GetMapping("/")
    public String home(){
//        log.info("home으로 가기....");
        return "index";
    }

}
