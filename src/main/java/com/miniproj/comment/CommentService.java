package com.miniproj.comment;


import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CommentService {

    List<CommentVO> selectAllComment(int boardNo);

    int registerComment(CommentDTO commentDTO);
}
