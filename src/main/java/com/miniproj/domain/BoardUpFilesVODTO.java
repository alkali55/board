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
}
