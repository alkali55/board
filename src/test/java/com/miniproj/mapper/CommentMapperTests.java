package com.miniproj.mapper;

import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class CommentMapperTests {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    public void testSelectAllCommentsWithPaging() {

        PagingRequestDTO pagingRequestDTO = PagingRequestDTO.builder()
                .pageNo(1)
                .pagingSize(10)
                .build();

        int boardNo = 1036;

        List<CommentVO> list = commentMapper.selectAllCommentsWithPaging(boardNo, pagingRequestDTO);

        log.info("list = {}", list);

        log.info("전체 댓글 갯수 = {}", commentMapper.selectTotalCommentCount(boardNo));
    }
}
