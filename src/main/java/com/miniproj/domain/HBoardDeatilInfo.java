package com.miniproj.domain;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HBoardDeatilInfo {

    private int boardNo;
    private String title;
    private String content;
    private LocalDateTime postDate;
    private int readCount;
    private int ref;
    private int step;
    private int refOrder;

    private MemberVO writer;

    private List<BoardUpFilesVODTO> upfiles; // 첨부파일 목록

}
