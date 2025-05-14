package com.miniproj.service;

import com.miniproj.domain.*;
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

    boolean modifyBoard(HBoardDTO board);

    HBoardDTO getBoardDetail(int boardNo);

    List<BoardUpFilesVODTO> viewFilesByBoardNo(int boardNo);
    
    // 게시글 목록 + 페이징
    PagingResponseDTO<HBoardPageDTO> getList(PagingRequestDTO pagingRequestDTO);

    // 게시글 삭제
    List<BoardUpFilesVODTO> removeBoard(int boardNo);

    // 게시글 목록 + 페이징 + 검색
    PagingResponseDTO<HBoardPageDTO> getListWithSearch(PagingRequestDTO pagingRequestDTO);
}
