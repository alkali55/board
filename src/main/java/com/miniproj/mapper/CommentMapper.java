package com.miniproj.mapper;

import com.miniproj.domain.CommentDTO;
import com.miniproj.domain.CommentVO;
import com.miniproj.domain.PagingRequestDTO;
import org.apache.ibatis.annotations.*;

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

    // 댓글 수정
    @Update("update comment set content = #{content} where commentNo = #{commentNo}")
    int updateComment(CommentDTO commentDTO);
    
    // 댓글 삭제
    @Delete("delete from comment where commentNo = #{commentNo}")
    int deleteComment(int commentNo);

    @Select("select * from comment where commentNo = #{commentNo}")
    CommentVO selectCommentByCommentNo(Integer commentNo);
}
