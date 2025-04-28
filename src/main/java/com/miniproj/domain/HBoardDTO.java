package com.miniproj.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class HBoardDTO {

    private int boardNo;

    @NotBlank(message = "제목은 필수 입력입니다")
    @Size(max = 20, message = "제목은 20자 이내로 작성해 주세요")
    private String title;

    @Size(max = 2000, message = "내용은 2000자 이내로 작성해 주세요")
    private String content;
    
    private String writer;

}
