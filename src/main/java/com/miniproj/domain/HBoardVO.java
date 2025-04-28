package com.miniproj.domain;

import lombok.*;

import java.sql.Timestamp;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HBoardVO {

    private int boardNo;
    private String title;
    private String content;
    private String writer;
    private Timestamp postDate;
    private int readCount;
    private int ref;
    private int step;
    private int refOrder;

}
