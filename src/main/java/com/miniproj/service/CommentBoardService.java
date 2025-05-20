package com.miniproj.service;

import com.miniproj.domain.*;

import java.util.List;

public interface CommentBoardService {

    // 글 조회
    List<HBoardVO> getAllBoards();

    // 글 저장 (업로드파일)
    void saveBoardWithFiles(CommBoardDTO board);
    
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
    
    // 좋아요
    int likeBoard(int boardNo, String who);

    // 해당글에 좋아요한 사람 수
    int countLikes(int boardNo);

    // 로그인 유저가 해당글을 좋아요 했는지 여부
    boolean hasLiked(int boardNo, String memberId);

    // 좋아요를 한 top N명의 아이디
    List<String> selectTopLikeMembers(int boardNo, int limit);
    
    // 좋아요 해제
    int dislikeBoard(int boardNo, String who);

    // 작성자 조회
    String findBoardWriter(int boardNo);
}
