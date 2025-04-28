package com.miniproj.mapper;

import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {

    @Select("select now()")
    String selectNow();

    @Insert("insert into hboard(title, content, writer) values(#{title}, #{content}, #{writer})")
    @Options(useGeneratedKeys = true, keyProperty = "boardNo")
    int insertNewBoard(HBoardDTO hBoardDTO);

    @Update("update hboard set ref = #{boardNo} where boardNo=#{boardNo}")
    int updateRefToBoardNo(@Param("boardNo") int boardNo);

    @Select("select * from hboard")
    List<HBoardVO> selectAllBoards();

}
