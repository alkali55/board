package com.miniproj.exception;

import com.miniproj.domain.MyResponseWithData;
import com.miniproj.domain.MyResponseWithoutData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 댓글이 존재하지 않을 때
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<MyResponseWithoutData> handleCommentNotFound(CommentNotFoundException e) {
        return ResponseEntity.badRequest().body(new MyResponseWithoutData(400, null, e.getMessage()));
    }
}
