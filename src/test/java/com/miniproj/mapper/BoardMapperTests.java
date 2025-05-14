package com.miniproj.mapper;


import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import com.miniproj.domain.PagingRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
@Slf4j
@Transactional // 테스트 메서드 실행 후 자동으로 롤백
public class BoardMapperTests {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testSelectNow(){

        log.info("now : {}", boardMapper.selectNow());
    }

    @Test
    public void testDataSource() throws SQLException {

        Connection conn = dataSource.getConnection();

        log.info("conn : {}", conn);
    }

    @Test
    @Rollback(value = false) // 자동롤백되지 않도록 설정
    public void testInsertNewBoard(){

        HBoardDTO hBoardDTO = HBoardDTO.builder()
                .title("제목 테스트 5")
                .content("내용 테스트 5")
                .writer("asdf123")
                .build();

        log.info("hBoardDTO : {}", hBoardDTO);
        // 1. 게시글 insert (boardNo는 자동생성)
        int result = boardMapper.insertNewBoard(hBoardDTO);
        int boardNo = hBoardDTO.getBoardNo();
        log.info("boardNo : {}", boardNo);

        // 2. ref 값 업데이트
        int resultUpdate = boardMapper.updateRefToBoardNo(boardNo);
        log.info("resultUpdate : {}", resultUpdate);
    }

    @Test
    public void testSelectAllBoard(){

        List<HBoardVO> list = boardMapper.selectAllBoards();

        for (HBoardVO vo : list){

            log.info("HBoardVO : {}", vo);
        }

    }

    @Test
    public void selectBoardDetailInfoByBoardNoTest(){

        log.info("resultMap : {}", boardMapper.selectBoardDetailInfoByBoardNo(1).get(0));
    }

    @Test
    @Rollback(value = false)
    public void insertDummyData(){

        for(int i = 0; i < 1000; i++){
            HBoardDTO dto = HBoardDTO.builder()
                    .title("dummy 제목 " + i)
                    .content("dummy 내용 "+ i)
                    .writer("asdf123")
                    .build();

            boardMapper.insertNewBoard(dto);
            boardMapper.updateRefToBoardNo(dto.getBoardNo());
        }
    }

    @Test
    public void testPaging(){
//        PagingRequestDTO pagingRequestDTO = new PagingRequestDTO();
        PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
                .pageNo(102)
                .pagingSize(10)
                .build();

        log.info("pagingRequestDTO = {}", pagingRequestDTO);
        log.info("skip = {}", pagingRequestDTO.getSkip());

        List<HBoardVO> list = boardMapper.selectList(pagingRequestDTO);
        for (HBoardVO hBoardVO : list) {
            log.info("pagination list = {}", hBoardVO);
        }

        log.info("전체 게시글 갯수 = {}", boardMapper.selectTotalCount());

        int pageNo = 77;
        log.info("end : {}", (((pageNo - 1) / 10) + 1) * 10);
//
//        int end = (int)(Math.ceil(pageNo / 10.0)) * 10;
    }

    @Test
    public void testSearch(){

        PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
                .pageNo(1)
                .pagingSize(10)
//                .type("tcw")
                .keyword("테스트")
                .build();

        log.info("pagingRequestDTO = {}", pagingRequestDTO);
        log.info("pagingRequestDTO.getSearchTypes() = {}", pagingRequestDTO.getSearchTypes());

//        List<HBoardVO> list = boardMapper.selectListWithSearch(pagingRequestDTO);
        List<HBoardVO> list = boardMapper.selectByMyQuery(pagingRequestDTO);

        for(HBoardVO hBoardVO : list){
            log.info("list with pagination and search = {}", hBoardVO);
        }
    }
}
