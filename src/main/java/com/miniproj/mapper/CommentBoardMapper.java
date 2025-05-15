package com.miniproj.mapper;

import com.miniproj.domain.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentBoardMapper {

    @Select("select now()")
    String selectNow();

    @Insert("insert into hboard(title, content, writer, boardType) values(#{title}, #{content}, #{writer}, 'cboard')")
    @Options(useGeneratedKeys = true, keyProperty = "boardNo")
    int insertNewBoard(HBoardDTO hBoardDTO);

    @Update("update hboard set ref = #{boardNo} where boardNo=#{boardNo} and boardType = 'cboard'")
    int updateRefToBoardNo(@Param("boardNo") int boardNo);

    @Select("select * from hboard where boardType = 'cboard' order by ref desc, refOrder asc")
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
    @Select("select ifnull((select Hour(timediff(now(), readwhen)) from boardreadlog where readWho = #{readWho} and boardNo = #{boardNo}), -1)")
    int selectDateDiffOrMinusOne(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Insert("insert into boardreadlog (readWho, boardNo) values (#{readWho}, #{boardNo})")
    int insertViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Update("update boardreadlog set readWhen = now() where readWho = #{readWho} and boardNo = #{boardNo}")
    int updateViewLog(@Param("readWho") String readWho, @Param("boardNo") int boardNo);

    @Update("update hboard set readcount = readcount + 1 where boardNo = #{boardNo}")
    int incrementReadCount(@Param("boardNo") int boardNo);

    // 답글
    @Update("update hboard set refOrder = refOrder + 1 where ref = #{ref} and refOrder > #{refOrder} and boardType = 'cboard'")
    int updateRefOrder(@Param("ref") int ref, @Param("refOrder") int refOrder);

    @Insert("insert into hboard(title, content, writer, ref, step, refOrder, boardType) values(#{title}, #{content}, #{writer}, #{ref}, #{step}, #{refOrder}, 'cboard')")
    @Options(useGeneratedKeys = true, keyProperty = "boardNo")
    int insertReplyBoard(HBoardDTO replyBoard);
    
    // 게시글 수정
    @Update("update hboard set title = #{title}, content = #{content} where boardNo = #{boardNo}")
    int updateBoard(HBoardDTO modifyBoard);

    @Delete("delete from boardupfiles where fileNo = #{fileNo}")
    int deleteFileByNo(int fileNo);

    @Select("select boardNo, title, content, writer, ref, postDate, readCount, ref, step, refOrder\n" +
            "from hboard where boardNo = #{boardNo}")
    HBoardDTO selectBoardDetail(int boardNo);

    @Select("select * from boardupfiles where boardNo = #{boardNo}")
    List<BoardUpFilesVODTO> selectFilesByBoardNo(int boardNo);

    // 페이징
    @Select("select * from hboard where boardType = 'cboard' order by ref desc, refOrder asc limit #{skip}, #{pagingSize}")
    List<HBoardVO> selectList(PagingRequestDTO pagingRequestDTO);

    @Select("select count(*) from hboard where boardType = 'cboard'")
    int selectTotalCount();

    // 게시글 삭제
    @Delete("delete from boardupfiles where boardNo = #{boardNo}")
    int deleteAllBoardUpFiles(int boardNo);

    @Update("update hboard set isDelete = 'Y', title = '', content = '' where boardNo = #{boardNo}")
    int deleteBoardByBoardNo(int boardNo);

    // 게시글 검색
    List<HBoardVO> selectListWithSearch(PagingRequestDTO pagingRequestDTO);

    List<HBoardVO> selectByMyQuery(PagingRequestDTO pagingRequestDTO);
    
    // 검색된 총 글의 갯수
    int selectTotalCountWithSearch(PagingRequestDTO pagingRequestDTO);
}
