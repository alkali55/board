package com.miniproj.mapper;


import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
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
                .title("제목 테스트4")
                .content("내용 테스트4")
                .writer("asdf123")
                .build();

        log.info("hBoardDTO : {}", hBoardDTO);
        int result = boardMapper.insertNewBoard(hBoardDTO);
        log.info("insert result : {}", result);
    }

    @Test
    public void testSelectAllBoard(){

        List<HBoardVO> list = boardMapper.selectAllBoards();

        for (HBoardVO vo : list){

            log.info("HBoardVO : {}", vo);
        }

    }

}
