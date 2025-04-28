package com.miniproj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {

    @GetMapping("/ex/content1")
    public void content1(Model model){
        model.addAttribute("arr", new String[]{"A", "B", "C"});
    }
}
