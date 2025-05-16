package com.miniproj.comment;


import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import com.miniproj.domain.PagingResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CommentService {

    List<CommentVO> selectAllComment(int boardNo);

    int registerComment(CommentDTO commentDTO);

    PagingResponseDTO<CommentVO> getAllCommentsWithPaging(int boardNo, PagingRequestDTO pagingRequestDTO);
}
