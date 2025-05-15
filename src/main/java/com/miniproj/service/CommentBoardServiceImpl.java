package com.miniproj.service;

import com.miniproj.domain.*;
import com.miniproj.mapper.BoardMapper;
import com.miniproj.mapper.CommentBoardMapper;
import com.miniproj.mapper.MemberMapper;
import com.miniproj.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentBoardServiceImpl implements CommentBoardService{

    private final MemberMapper memberMapper;

    private final CommentBoardMapper boardMapper;

    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<HBoardVO> getAllBoards() {
        return boardMapper.selectAllBoards();
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 하나의 커넥션으로 모든 dml 문이 성공하면 커밋, 중간에 하나라도 실패하면 롤백
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

        // 3. 포인트 계산
            PointWhy reason = PointWhy.WRITE;
            int score = memberMapper.selectPointScore(reason);

        // 4. 포인트 로그 저장
            memberMapper.insertPointLog(board.getWriter(), reason, score);


        // 5. 멤버 포인트 업데이트
            memberMapper.updateMemberPoint(board.getWriter(), score);

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

        } else if (dateDiff >= 24){
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean modifyBoard(HBoardDTO modifyBoard) {

//        for (BoardUpFilesVODTO upfile : modifyBoard.getUpfiles()) {
//            log.info("upfile = {}", upfile.getOriginalFileName());
//        }

//        for (BoardUpFilesVODTO file : modifyBoard.getUpfiles()) {
//            if (file.getFileStatus() == BoardUpFileStatus.DELETE) {
//                // 물리적 파일 삭제
//                fileUploadUtil.deleteFile(file.getFilePath());
//
//                // 이미지인 경우 섬네일도 삭제
//                if(file.isImage()){
//                    fileUploadUtil.deleteFile(file.getThumbFileName());
//                }
//            }
//        }


        // 1. 게시글 제목, 내용 수정
        if (boardMapper.updateBoard(modifyBoard) == 1){
            // 2. modifyFileList를 순회하면서 파일 처리 (DB)
            for (BoardUpFilesVODTO file : modifyBoard.getUpfiles()) {

                if(file.getFileStatus() == BoardUpFileStatus.INSERT){
                    // 새로 추가할 파일 insert
                    boardMapper.insertUploadFile(file);
                } else if (file.getFileStatus() == BoardUpFileStatus.DELETE){
                    // 삭제 요청된 파일 delete
                    boardMapper.deleteFileByNo(file.getFileNo());

                    // 물리적 파일 삭제
                    fileUploadUtil.deleteFile(file.getFilePath());
                    
                    // 이미지인 경우 섬네일도 삭제
                    if(file.isImage()){
                        fileUploadUtil.deleteFile(file.getThumbFileName());
                    }
                }

            }

        };



        return false;
    }

    @Override
    public HBoardDTO getBoardDetail(int boardNo) {

        return boardMapper.selectBoardDetail(boardNo);
    }

    @Override
    public List<BoardUpFilesVODTO> viewFilesByBoardNo(int boardNo) {
        return boardMapper.selectFilesByBoardNo(boardNo);
    }

    @Override
    public PagingResponseDTO<HBoardPageDTO> getList(PagingRequestDTO pagingRequestDTO) {

        List<HBoardVO> voList = boardMapper.selectList(pagingRequestDTO);

//        for (HBoardVO hBoardVO : voList) {
//            log.info("hBoardVO : {}", hBoardVO);
//        }

        List<HBoardPageDTO> dtoList = new ArrayList<>();

        for (HBoardVO vo : voList) {
            HBoardPageDTO dto = HBoardPageDTO.builder()
                    .boardNo(vo.getBoardNo())
                    .title(vo.getTitle())
                    .content(vo.getContent())
                    .writer(vo.getWriter())
                    .postDate(vo.getPostDate().toLocalDateTime())
                    .readCount(vo.getReadCount())
                    .ref(vo.getRef())
                    .step(vo.getStep())
                    .refOrder(vo.getRefOrder())
                    .isDelete(vo.getIsDelete())
                    .build();

            dtoList.add(dto);
        }

//        log.info("dtoList : {}", dtoList);

        int totalCount = boardMapper.selectTotalCount();
        return PagingResponseDTO.<HBoardPageDTO>allInfo()
                .pagingRequestDTO(pagingRequestDTO)
                .dtoList(dtoList)
                .total(totalCount)
                .build();

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BoardUpFilesVODTO> removeBoard(int boardNo) {

        // 1) 실제 파일을 하드에서도 삭제해야하므로, 삭제 하기 전에 첨부파일 정보 조회
        List<BoardUpFilesVODTO> fileList = boardMapper.selectFilesByBoardNo(boardNo);
//        log.info("fileList == null? {}", fileList == null);
//        log.info("fileList.isEmpty() {}", fileList.isEmpty());

        // 2) boardNo번 글의 첨부파일 정보를 DB에서 삭제
        boardMapper.deleteAllBoardUpFiles(boardNo);

        // 3) boardNo번 글을 삭제 (soft delete 방식 : isDelete = "Y" 업데이트)
        boardMapper.deleteBoardByBoardNo(boardNo);

        if(!fileList.isEmpty()){
            return fileList;
        } else {
            return null;
        }
    }

    @Override
    public PagingResponseDTO<HBoardPageDTO> getListWithSearch(PagingRequestDTO pagingRequestDTO) {

        List<HBoardVO> voList = boardMapper.selectListWithSearch(pagingRequestDTO);

        List<HBoardPageDTO> dtoList = new ArrayList<>();

        for (HBoardVO vo : voList) {
            HBoardPageDTO dto = HBoardPageDTO.builder()
                    .boardNo(vo.getBoardNo())
                    .title(vo.getTitle())
                    .content(vo.getContent())
                    .writer(vo.getWriter())
                    .postDate(vo.getPostDate().toLocalDateTime())
                    .readCount(vo.getReadCount())
                    .ref(vo.getRef())
                    .step(vo.getStep())
                    .refOrder(vo.getRefOrder())
                    .isDelete(vo.getIsDelete())
                    .build();

            dtoList.add(dto);
        }

//        log.info("dtoList : {}", dtoList);

        int totalCount = boardMapper.selectTotalCountWithSearch(pagingRequestDTO);

        return PagingResponseDTO.<HBoardPageDTO>allInfo()
                .pagingRequestDTO(pagingRequestDTO)
                .dtoList(dtoList)
                .total(totalCount)
                .build();
    }
}
