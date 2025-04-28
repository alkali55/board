package com.miniproj.controller;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public String list(Model model){

        List<HBoardVO> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "/board/list";


    }

    @GetMapping("/register")
    public String registerGET(){

        return "/board/register";
    }

    @PostMapping("/register")
    public String writeBoard( @ModelAttribute("board") HBoardDTO board){

    }

}
