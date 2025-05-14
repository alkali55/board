package com.miniproj.controller;

import com.miniproj.domain.*;
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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
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

    private List<BoardUpFilesVODTO> modifyFileList;

//    @GetMapping("/list")
//    public String list(Model model){
//
//        List<HBoardVO> boards = boardService.getAllBoards();
//        model.addAttribute("boards", boards);
//        return "/board/list";
//
//
//    }
    
    // --- 페이징 적용하여 list조회
//    @GetMapping("/list")
//    public String list(PagingRequestDTO pagingRequestDTO, Model model){
//
//        log.info("pagingRequestDTO = {}", pagingRequestDTO);
//        PagingResponseDTO<HBoardPageDTO> responseDTO = boardService.getList(pagingRequestDTO);
//
//        log.info("responseDTO : {}", responseDTO.getDtoList());
//
//        model.addAttribute("responseDTO", responseDTO);
//        return "/board/list";
//
//
//    }

    // --- 페이징 + 검색 적용하여 list 조회
    @GetMapping("/list")
    public String list(PagingRequestDTO pagingRequestDTO, Model model){

        log.info("pagingRequestDTO = {}", pagingRequestDTO);
        PagingResponseDTO<HBoardPageDTO> responseDTO = boardService.getListWithSearch(pagingRequestDTO);

        log.info("responseDTO : {}", responseDTO.getDtoList());

        model.addAttribute("responseDTO", responseDTO);
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
    public String boardDetail(@RequestParam(value = "boardNo", required = false, defaultValue = "-1") int boardNo,
                              PagingRequestDTO pagingRequestDTO,
                              RedirectAttributes redirectAttributes,
                              Model model, HttpServletRequest request){

        log.info("게시판 상세보기 호출....boardNo = {}", boardNo);
        log.info("클라이언트 ip = {}", GetClientIPAddr.getClientIP(request));

        // 잘못된 요청 : boardNo가 없는 경우
        if (boardNo == -1){
            redirectAttributes.addFlashAttribute("error", "잘못된 접근입니다.");
            return "redirect:/board/list";
        }

        String ipAddr = GetClientIPAddr.getClientIP(request);
//        List<HBoardDeatilInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);

        List<HBoardDeatilInfo> detailInfos = boardService.viewBoardByNo(boardNo, ipAddr);
        log.info("detailInfos = {}", detailInfos.get(0));


        if("Y".equals(detailInfos.get(0).getIsDelete())){
            // 조회 결과가 삭제된 글이면 다시 목록으로
            redirectAttributes.addFlashAttribute("error", "삭제된 글이거나 존재하지 않는 게시글입니다");
            return "redirect:/board/list";
        }

        model.addAttribute("detail", detailInfos.get(0));
//        model.addAttribute("pagingRequestDTO", pagingRequestDTO);
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

        if(bindingResult.hasErrors()){
            return "/board/replyForm";
        }

//        for (MultipartFile mpf : reply.getMultipartFiles()){
//            log.info("reply 업로드 파일 이름 : {}", mpf.getOriginalFilename());
//        }

        List<BoardUpFilesVODTO> upFilesVODTOS = fileUploadUtil.saveFiles(reply.getMultipartFiles());
        reply.setUpfiles(upFilesVODTOS);

        boardService.saveReply(reply);

        return "redirect:/board/list";
    }

    // 게시글 수정
    @GetMapping("/modify")
    public String showModifyForm(@RequestParam("boardNo") int boardNo, PagingRequestDTO pagingRequestDTO, Model model){

        log.info("{} 번 글 수정하자", boardNo);

//        List<HBoardDeatilInfo> detailInfos = boardService.viewBoardDetailInfoByNo(boardNo);
//
//        this.modifyFileList = detailInfos.get(0).getUpfiles();
//
//        model.addAttribute("board", detailInfos.get(0));

        HBoardDTO board= boardService.getBoardDetail(boardNo);
        List<BoardUpFilesVODTO> upFiles = boardService.viewFilesByBoardNo(boardNo);
        this.modifyFileList = upFiles;
//        log.info("모디파이파일!!! : {}", modifyFileList);
        board.setUpfiles(upFiles);
        model.addAttribute("board", board);

        return "/board/modify";
    }

    @PostMapping(value = "/modifyRemoveFileCheck", produces = "application/json; charset=UTF-8")
    public ResponseEntity<MyResponseWithoutData> modifyRemoveFileCheck(@RequestParam("removeFileNo") int removeFilePK){

        log.info("{} 번 파일을 삭제 처리하자", removeFilePK);

        for (BoardUpFilesVODTO file : modifyFileList) {

            if(removeFilePK == file.getFileNo()){
                file.setFileStatus(BoardUpFileStatus.DELETE); // 삭제 예정 표시
            }
        }

        outputCurModifyFileList(); // 현재 수정 파일 리스트 출력

        return ResponseEntity.ok(new MyResponseWithoutData(200, null, "success"));
    }

    private void outputCurModifyFileList() {

        log.info("------------------------------------------------------------------------");
        log.info("현재 파일리스트에 있는 파일 (글 수정)");
        for (BoardUpFilesVODTO file : modifyFileList) {
            log.info("파일 : {}", file);
        }
        log.info("------------------------------------------------------------------------");
    }

    @PostMapping("/cancelRemFiles")
    public ResponseEntity<MyResponseWithoutData> cancelRemFiles(){

        log.info("파일리스트의 모든 파일 삭제 취소 처리");

        for (BoardUpFilesVODTO file : modifyFileList) {
            file.setFileStatus(null); // 삭제 표시 해제
        }

        outputCurModifyFileList();

        return ResponseEntity.ok(new MyResponseWithoutData(200, null, "success"));
    }

    @PostMapping("/modifyBoardSave")
    public String modifyBoardSave(@Valid @ModelAttribute("board") HBoardDTO board, BindingResult bindingResult, @ModelAttribute("pagingRequestDTO") PagingRequestDTO pagingRequestDTO,
                                  @RequestParam(value = "modifyNewFile", required = false) MultipartFile[] modifyNewFile, RedirectAttributes redirectAttributes) {

        String link = pagingRequestDTO.getLink();
        if (bindingResult.hasErrors()) {

            // 수정 폼으로 리다이렉트
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.board", bindingResult);
            redirectAttributes.addFlashAttribute("board", board);

            for (ObjectError error : bindingResult.getAllErrors()) {
                log.info("에러 메세지 : {}", error.getDefaultMessage());
            }

            return "redirect:/board/modify?boardNo=" + board.getBoardNo() + "&" + link;
        }

        log.info("수정하자 POST HBoardDTO = {}", board);
//        log.info("추가 파일 = {}", modifyNewFile);

        // 새로 업로드된 파일 저장
        try{
            if (modifyNewFile != null && modifyNewFile.length > 0) {

                List<MultipartFile> fileList = new ArrayList<>();
                for (MultipartFile file : modifyNewFile) {
                    if (!file.isEmpty()) {
                        fileList.add(file);
                    }
                }

                if (!fileList.isEmpty()) {

                    List<BoardUpFilesVODTO> savedFiles = fileUploadUtil.saveFiles(fileList);
                    for (BoardUpFilesVODTO fileInfo : savedFiles) {
                        fileInfo.setFileStatus(BoardUpFileStatus.INSERT);
                        fileInfo.setBoardNo(board.getBoardNo());
                        modifyFileList.add(fileInfo);
                    }
                }
            }

            outputCurModifyFileList();

            // 최종 수정 저장
    //        log.info("modifyFileList[0] : boardNo = {}", modifyFileList.get(0).getBoardNo());
            board.setUpfiles(modifyFileList);

            boardService.modifyBoard(board);
            redirectAttributes.addAttribute("status", "success");

        } catch (Exception e){
            log.error("게시글 수정 실패", e);
            redirectAttributes.addAttribute("status", "fail");
        }


        return "redirect:/board/detail?boardNo=" + board.getBoardNo() + "&" + link;
    }

    // 게시글 삭제
    @RequestMapping("/removeBoard")
    public String removeBoard(@RequestParam("boardNo") int boardNo, RedirectAttributes redirectAttributes){

        log.info("boardNo = {} 번 글을 삭제하자", boardNo);

        try {
            List<BoardUpFilesVODTO> fileList = boardService.removeBoard(boardNo);

            // 첨부파일이 있다면 ... 하드에서 삭제
            if (fileList != null) {
                for (BoardUpFilesVODTO boardUpFilesVODTO : fileList) {
                    fileUploadUtil.deleteFile(boardUpFilesVODTO.getFilePath());
                    // 이미지인 경우 썸네일도 삭제
                    if (boardUpFilesVODTO.isImage()) {
                        fileUploadUtil.deleteFile(boardUpFilesVODTO.getThumbFileName());
                    }

                }
            }
            redirectAttributes.addAttribute("status", "removesuccess");
        } catch (Exception e){
            redirectAttributes.addAttribute("status", "removefail");
        }

        return "redirect:/board/list";
    }
}
