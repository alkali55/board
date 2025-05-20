package com.miniproj.comment;

import com.miniproj.domain.*;
import com.miniproj.exception.CommentNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // = @Controller + @ResponseBody
@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/all/{boardNo}/{pageNo}")
    public ResponseEntity<MyResponseWithData> getAllCommentByBoardNo(@PathVariable("boardNo") int boardNo,
                                                                     @PathVariable("pageNo") int pageNo, HttpSession session) {

        log.info("댓글형 게시판 : {}번 글의 {} 페이지 댓글 조회....", boardNo, pageNo);
//        List<CommentVO> result = commentService.selectAllComment(boardNo);

        Member loginMember = (Member)session.getAttribute("loginMember");

        String loginMemberId = (loginMember != null) ? loginMember.getMemberId() : null;

        PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
                .pageNo(pageNo)
                .pagingSize(10)
                .build();

        PagingResponseDTO<CommentVO> responseDTO = commentService.getAllCommentsWithPaging(boardNo, pagingRequestDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("responseDTO", responseDTO);
        result.put("loginMemberId", loginMemberId);
        return ResponseEntity.ok(MyResponseWithData.success(result));
    }

    @PostMapping(value = "/{boardNo}", produces = {"application/json; charset=utf-8"})
    // , produces = {"application/json; charset=uft-8"}
    public ResponseEntity<MyResponseWithData> saveComment(@PathVariable("boardNo") int boardNo,
                                                          @RequestBody CommentDTO commentDTO){

        commentDTO.setBoardNo(boardNo);

        log.info("댓글 저장 요청 : {}", commentDTO);

        try {
            int result = commentService.registerComment(commentDTO);
            return ResponseEntity.ok(MyResponseWithData.success());
        } catch (Exception e) {
            return ResponseEntity.ok(MyResponseWithData.fail());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("internalServerError");
        }

//        if(result == 1){
//            return ResponseEntity.ok("success");
//        } else {
//            return ResponseEntity.ok("fail");
//        }


    }

//    @PatchMapping(value = "/{commentNo}")
    @PatchMapping("/{commentNo}")
    public ResponseEntity<MyResponseWithoutData> modifyComment(@PathVariable("commentNo") Integer commentNo,
                                                            @RequestBody CommentDTO commentDTO, HttpSession session){

        log.info("댓글 수정 요청 : commentNo = {}, content = {}", commentNo, commentDTO);
        Member loginMember = (Member)session.getAttribute("loginMember");

        if(loginMember == null){
//            return ResponseEntity.ok(new MyResponseWithoutData(401, null, "NOT_LOGGED_IN"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MyResponseWithoutData(401, null, "NOT_LOGGED_IN"));
        }

        CommentVO commentVO = commentService.getCommentByCommentNo(commentNo);
        if(commentVO == null){
//            return ResponseEntity.ok(new MyResponseWithoutData(404, null, "COMMENT_NOT_FOUND"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MyResponseWithoutData(404, null, "COMMENT_NOT_FOUND"));
        }


        if(!loginMember.getMemberId().equals(commentVO.getCommenter())){
//            return ResponseEntity.ok(new MyResponseWithoutData(403, null, "FORBIDDEN"));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MyResponseWithoutData(403, null, "NOT_LOGGED_IN"));
        }



        try{
            commentDTO.setCommentNo(commentNo);
            commentService.updateComment(commentDTO);
            return ResponseEntity.ok(new MyResponseWithoutData(200, null, "SUCCESS"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MyResponseWithoutData(400, null, "FAIL"));
        }

    }

    @DeleteMapping("/{commentNo}")
    public ResponseEntity<MyResponseWithoutData> deleteComment(@PathVariable("commentNo") Integer commentNo, HttpSession session) throws CommentNotFoundException {

        Member loginMember = (Member)session.getAttribute("loginMember");

//        if(loginMember == null){
//            return ResponseEntity.ok(new MyResponseWithoutData(401, null, "NOT_LOGGED_IN"));
//        }
//
//        CommentVO commentVO = commentService.getCommentByCommentNo(commentNo);
//        if(commentVO == null){
//            return ResponseEntity.ok(new MyResponseWithoutData(404, null, "COMMENT_NOT_FOUND"));
//        }
//
//        if(!loginMember.getMemberId().equals(commentVO.getCommenter())){
//            return ResponseEntity.ok(new MyResponseWithoutData(403, null, "FORBIDDEN"));
//        }

        log.info("댓글 삭제 요청 : commentNo = {}", commentNo);

        int result = commentService.deleteComment(commentNo);
        if(result == 1){
            return ResponseEntity.ok(new MyResponseWithoutData(200, null, "SUCCESS"));
        } else {
            throw new CommentNotFoundException("삭제할 댓글이 존재하지 않습니다.");
        }
            // return ResponseEntity.internalServerError().body(new MyResponseWithoutData(400, null, "FAIL"));

    }


}
