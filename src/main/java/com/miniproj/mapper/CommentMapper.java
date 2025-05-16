package com.miniproj.mapper;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("select * from comment where boardNo = #{boardNo}")
    List<CommentVO> selectAllComment(int boardNo);

    @Insert("insert into comment(commenter, content, boardNo) values(#{commenter}, #{content}, #{boardNo})")
    int insertComment(CommentDTO commentDTO);

    @Select("select * from comment where boardNo = #{boardNo} order by commentNo desc limit #{pagingRequestDTO.skip}, #{pagingRequestDTO.pagingSize}")
    List<CommentVO> selectAllCommentsWithPaging(@Param("boardNo") int boardNo, @Param("pagingRequestDTO") PagingRequestDTO pagingRequestDTO);

    @Select("select count(*) from comment where boardNo = #{boardNo}")
    int selectTotalCommentCount(int boardNo);

}
