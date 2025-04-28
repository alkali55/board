package com.miniproj.controller;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String registerGET(Model model){

        HBoardDTO board = new HBoardDTO();
        model.addAttribute("board", board);
        return "/board/register";
    }

    @PostMapping("/register")
    public String writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult){
        // @Valid를 쓰면 spring이 Bean Validation호출하고 Hibernate Validator가 동작하고
        // 그 결과를 BindingResult에 담는다.
        if (bindingResult.hasErrors()){
            return "/board/register";
        }

        log.info("board : {}", board);
        return "redirect:/board/list";
    }

}
