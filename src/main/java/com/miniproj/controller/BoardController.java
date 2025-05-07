package com.miniproj.controller;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDeatilInfo;
import com.miniproj.domain.HBoardVO;
import com.miniproj.service.BoardService;
import com.miniproj.util.FileUploadUtil;
import com.miniproj.util.GetClientIPAddr;
import jakarta.servlet.http.HttpServletRequest;
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


// 컨트롤러단에서 해야 할일
// 1) URI 매핑
// 2) view 단에 보내준 매개 변수 수집
// 3) 데이터베이스에 대한 CRUD를 수행하기 위해 service단의 메서드 호출
// 4) 부가적으로 HttpServletRequest, HttpServletResponse, HttpSession 등의
//    Servlet 객체들을 이용할 수 있다. 이러한 객체를 이용해서 구현할 기능이 있으면 그 기능은 컨트롤러에 구현한다.
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
    public String boardDetail(@RequestParam(value = "boardNo") int boardNo, Model model, HttpServletRequest request){

        log.info("게시판 상세보기 호출....boardNo = {}", boardNo);
        log.info("클라이언트 ip = {}", GetClientIPAddr.getClientIP(request));

        String ipAddr = GetClientIPAddr.getClientIP(request);
//        List<HBoardDeatilInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);

        List<HBoardDeatilInfo> detailInfos = boardService.viewBoardByNo(boardNo, ipAddr);
        model.addAttribute("detail", detailInfos.get(0));
        log.info("첨부파일 리스트 : {}", detailInfos.get(0).getUpfiles());
        return "/board/detail";
    }

    // 답글 등록폼
    @GetMapping("/showReplyForm")
    public String showReplyForm(@RequestParam("ref") int ref, @RequestParam("step") int step,
                                @RequestParam("refOrder") int refOrder, Model model){
        log.info("replyForm 요청....");

        HBoardDTO reply = new HBoardDTO();
        reply.setRef(ref);
        reply.setStep(step);
        reply.setRefOrder(refOrder);

        model.addAttribute("reply", reply);
        return "/board/replyForm";
    }

    // 답글 저장
    @PostMapping("/saveReply")
    public String saveReply(@Valid @ModelAttribute("reply") HBoardDTO reply, BindingResult bindingResult) throws IOException {

        log.info("reply = {}", reply);

        for (MultipartFile mpf : reply.getMultipartFiles()){
            log.info("reply 업로드 파일 이름 : {}", mpf.getOriginalFilename());
        }

        List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(reply.getMultipartFiles());
        reply.setUpfiles(upFilesVODTOS);

        boardService.saveReply(reply);

        return "redirect:/board/list";
    }

}
