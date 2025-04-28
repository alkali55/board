package com.miniproj.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HBoardDTO {

    private String title;
    private String content;
    private String writer;

}
