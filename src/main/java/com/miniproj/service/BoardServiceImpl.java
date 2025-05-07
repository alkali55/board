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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<HBoardDeatilInfo> viewBoardByNo(int boardNo, String ipAddr) {
        // 상세조회
        List<HBoardDeatilInfo> boardInfo = boardMapper.selectBoardDetailInfoByBoardNo(boardNo);

        // 조회수 처리
        int dateDiff = boardMapper.selectDateDiffOrMinusOne(ipAddr, boardNo);

        if (dateDiff == -1){
            // ipAddr유저가 boardNo번 글을 조회한 적이 없다 -> 조회 내역 저장 -> 조회수 증가
            if (boardMapper.insertViewLog(ipAddr, boardNo) == 1){
                // 조회수 증가
                if (boardMapper.incrementReadCount(boardNo) == 1){
                    for(HBoardDeatilInfo b : boardInfo){
                        b.setReadCount(b.getReadCount() + 1);
                    }
                }
            }

        } else if (dateDiff >= 1){
            // ipAddr유저가 boardNo번 글을 조회한 적이 있고 24시간 이후 -> 조회 내역 수정 -> 조회수 증가
            boardMapper.updateViewLog(ipAddr, boardNo);
            if (boardMapper.incrementReadCount(boardNo) == 1){
                for(HBoardDeatilInfo b : boardInfo){
                    b.setReadCount(b.getReadCount() + 1);
                }
            }
        }

        return boardInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveReply(HBoardDTO replyBoard) {
        // 답글 저장
        // 부모글에 대한 다른 답글이 있는 상태에서, 부모글의 답글이 추가되는 경우
        // (자리확보를 위해) 기존의 답글의 refOrder값을 update해야 한다.

        boardMapper.updateRefOrder(replyBoard.getRef(), replyBoard.getRefOrder());

        // 답글을 입력받아서 답글을 저장하고, 부모글의 boardNo를 ref에,
        // 부모글의 step + 1을 step에, 부모글의 refOrder + 1을 refOrder에 저장한다.
        replyBoard.setStep(replyBoard.getStep() + 1);
        replyBoard.setRefOrder(replyBoard.getRefOrder() + 1);

        if(boardMapper.insertReplyBoard(replyBoard) == 1){

            if (replyBoard.getUpfiles() != null && !replyBoard.getUpfiles().isEmpty()){
                for (BoardUpFilesVODTO file :  replyBoard.getUpfiles()){
                    file.setBoardNo(replyBoard.getBoardNo()); // FK 값
                    boardMapper.insertUploadFile(file);
                }
            }
        };

    }
}
