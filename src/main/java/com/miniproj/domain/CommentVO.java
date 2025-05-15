package com.miniproj.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CommentVO {

    private int commentNo;
    private String commenter;
    private String content;
    private LocalDateTime regDate;
    private int boardNo;

}
