package com.miniproj.mapper;

import com.miniproj.domain.BoardUpFilesVODTO;
import com.miniproj.domain.HBoardDTO;
import com.miniproj.domain.HBoardDeatilInfo;
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

    @Select("select * from hboard order by ref desc, refOrder asc")
    List<HBoardVO> selectAllBoards();

    // 파일 저장
    @Insert("""
            insert into boardupfiles (
            	boardNo, originalFileName, newFileName, thumbFileName, isImage, ext, size, base64, filePath
            ) values (
            	#{boardNo}, #{originalFileName}, #{newFileName}, #{thumbFileName}, #{isImage}, #{ext}, #{size}, #{base64}, #{filePath}
            )
            """)
    int insertUploadFile(BoardUpFilesVODTO file);

    // resultMap 이용한 조인문
    List<HBoardDeatilInfo> selectBoardDetailInfoByBoardNo(int boardNo);

    // 게시글 상세 조회 + 조회수 처리 관련 쿼리문
    @Select("select ifnull((select DATEDIFF(now(), readWhen) from boardreadlog where readWho = #{readWho} and boardNo = #{boardNo}), -1)")
    int selectDateDiffOrMinusOne(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Insert("insert into boardreadlog (readWho, boardNo) values (#{readWho}, #{boardNo})")
    int insertViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Update("update boardreadlog set readWhen = now() where readWho = #{readWho} and boardNo = #{boardNo}")
    int updateViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Update("update hboard set readcount = readcount + 1 where boardNo = #{boardNo}")
    int incrementReadCount(@Param("boardNo") int boardNo);

    // 답글
    @Update("update hboard set refOrder = refOrder + 1 where ref = #{ref} and refOrder > #{refOrder}")
    int updateRefOrder(@Param("ref") int ref, @Param("refOrder") int refOrder);

    @Insert("insert into hboard(title, content, writer, ref, step, refOrder) values(#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "boardNo")
    int insertReplyBoard(HBoardDTO replyBoard);
}
