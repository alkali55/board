package com.miniproj.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BoardUpFilesVODTO {

    private int fileNo;
    private String originalFileName;
    private String newFileName;
    private String thumbFileName;
    private boolean isImage;
    private String ext;
    private long size;
    private int boardNo;
    private String base64;
    private String filePath;

    // 게시글 수정시 첨부파일의 상태를 기록하는 변수 ("INSERT" : 새로 저장되는 파일, "DELETE" : 삭제할 파일)
    private BoardUpFileStatus fileStatus;
}
