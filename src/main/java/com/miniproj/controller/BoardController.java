package com.miniproj.controller;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.service.BoardService;
import com.miniproj.util.FileUploadUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;
    private final FileUploadUtil fileUploadUtil;

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

//    @PostMapping("/register")
//    public String writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult){
//        // @Valid를 쓰면 spring이 Bean Validation호출하고 Hibernate Validator가 동작하고
//        // 그 결과를 BindingResult에 담는다.
//        if (bindingResult.hasErrors()){
//            return "/board/register";
//        }
//
//        log.info("board : {}", board);
//        return "redirect:/board/list";
//    }

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity writeBoard(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult) throws IOException {
        // @Valid를 쓰면 spring이 Bean Validation호출하고 Hibernate Validator가 동작하고
        // 그 결과를 BindingResult에 담는다.

        log.info("글 저장 요청 - HBoardDTO : {}", board);

        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();

            log.info("fieldErrors: {}", bindingResult.getFieldErrors());
            for(FieldError error : bindingResult.getFieldErrors()){
                log.info("field : {}, msg : {}", error.getField(), error.getDefaultMessage());
                errors.put(error.getField(), error.getDefaultMessage());
            }
            log.info("errors map : {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

//        for (MultipartFile mpf : board.getMultipartFiles()){
//            log.info("업로드 파일 이름 : {}", mpf.getOriginalFilename());
//        }

        List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(board.getMultipartFiles());
        board.setUpfiles(upFilesVODTOS);

        boardService.saveBoardWithFiles(board);

//        for (BoardUpFilesVODTO dto : upFilesVODTOS){
//            log.info("upfile dto : {}", dto);
//        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/detail")
    public String boardDetail(@RequestParam(value = "boardNo") int boardNo){

        log.info("게시판 상세보기 호출....boardNo = {}", boardNo);

        boardService.viewBoardByNo(boardNo);

        return "/board/detail";
    }

}
