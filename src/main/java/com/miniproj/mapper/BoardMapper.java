package com.miniproj.mapper;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("select now()")
    String selectNow();

    @Insert("insert into hboard(title, content, writer) values(#{title}, #{content}, #{writer})")
    int insertNewBoard(HBoardDTO hBoardDTO);

    @Select("select * from hboard")
    List<HBoardVO> selectAllBoards();

}
