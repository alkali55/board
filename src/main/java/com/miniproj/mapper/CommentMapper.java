package com.miniproj.mapper;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("select * from comment where boardNo = #{boardNo}")
    List<CommentVO> selectAllComment(int boardNo);

    @Insert("insert into comment(commenter, content, boardNo) values(#{commenter}, #{content}, #{boardNo})")
    int insertComment(CommentDTO commentDTO);
}
