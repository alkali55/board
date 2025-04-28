package com.miniproj.service;

import com.miniproj.domain.HBoardVO;
import com.miniproj.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{

    private final BoardMapper boardMapper;

    @Override
    public List<HBoardVO> getAllBoards() {
        return boardMapper.selectAllBoards();
    }
}
