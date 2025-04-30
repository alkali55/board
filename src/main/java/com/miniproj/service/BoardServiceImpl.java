package com.miniproj.service;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDeatilInfo;
import com.miniproj.domain.HBoardVO;
import com.miniproj.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardMapper boardMapper;

    @Override
    public List<HBoardVO> getAllBoards() {
        return boardMapper.selectAllBoards();
    }

    @Override
    @Transactional // 하나의 커넥션으로 모든 dml 문이 성공하면 커밋, 중간에 하나라도 실패하면 롤백
    public void saveBoardWithFiles(HBoardDTO board) {
        // 1. 게시글 저장
            boardMapper.insertNewBoard(board);
            boardMapper.updateRefToBoardNo(board.getBoardNo());

        // 2. 첨부파일 저장
            if (board.getUpfiles() != null && !board.getUpfiles().isEmpty()){
                for (BoardUpFilesVODTO file :  board.getUpfiles()){
                    file.setBoardNo(board.getBoardNo()); // FK 값
                    boardMapper.insertUploadFile(file);
                }
            }
    }

    @Override
    public void viewBoardByNo(int boardNo) {

    }

    @Override
    public List<HBoardDeatilInfo> viewBoardDetailInfoByNo(int boardNo) {
        return boardMapper.selectBoardDetailInfoByBoardNo(boardNo);
    }
}
