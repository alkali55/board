package com.miniproj.comment;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import com.miniproj.domain.PagingResponseDTO;
import com.miniproj.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    @Override
    public List<CommentVO> selectAllComment(int boardNo) {

        return commentMapper.selectAllComment(boardNo);
    }

    @Override
    public int registerComment(CommentDTO commentDTO) {
        return commentMapper.insertComment(commentDTO);
    }

    @Override
    public PagingResponseDTO<CommentVO> getAllCommentsWithPaging(int boardNo, PagingRequestDTO pagingRequestDTO) {

        List<CommentVO> list = commentMapper.selectAllCommentsWithPaging(boardNo, pagingRequestDTO);
        int totalCount = commentMapper.selectTotalCommentCount(boardNo);

        return PagingResponseDTO.<CommentVO>allInfo()
                .pagingRequestDTO(pagingRequestDTO)
                .dtoList(list)
                .total(totalCount)
                .build();
    }


}
