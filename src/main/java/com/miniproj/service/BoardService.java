package com.miniproj.service;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDeatilInfo;
import com.miniproj.domain.HBoardVO;
import jakarta.validation.Valid;

import java.util.List;

public interface BoardService {

    // 글 조회
    List<HBoardVO> getAllBoards();

    // 글 저장 (업로드파일)
    void saveBoardWithFiles(HBoardDTO board);
    
    // 글 상세 조회
    void viewBoardByNo(int boardNo);

    // 글 상세 조회 : resultMap
    List<HBoardDeatilInfo> viewBoardDetailInfoByNo(int boardNo);

    // 글 상세 조회 + 조회수 처리
    List<HBoardDeatilInfo> viewBoardByNo(int boardNo, String ipAddr);
    
    // 답글 저장
    void saveReply(HBoardDTO reply);
}
