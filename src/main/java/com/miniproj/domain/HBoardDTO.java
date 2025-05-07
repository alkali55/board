package com.miniproj.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @NotBlank(message = "내용은 필수 입력입니다")
    @Size(max = 2000, message = "내용은 2000자 이내로 작성해 주세요")
    private String content;
    
    private String writer;

    private int ref;
    private int step;
    private int refOrder;

    // 업로드된 파일들
    private List<MultipartFile> multipartFiles;

    // 실제 저장된 파일 정보
    private List<BoardUpFilesVODTO> upfiles;
}
