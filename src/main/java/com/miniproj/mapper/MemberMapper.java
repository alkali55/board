package com.miniproj.mapper;

import com.miniproj.domain.Member;
import com.miniproj.domain.PointWhy;
import org.apache.ibatis.annotations.*;

@Mapper
public interface MemberMapper {

    @Select("select pointScore from pointdef where pointWhy = #{pointWhy}")
    int selectPointScore(PointWhy pointWhy);

    @Insert("insert into pointlog (pointWho, pointWhy, pointScore) values(#{pointWho}, #{pointWhy}, #{pointScore})")
    int insertPointLog(@Param("pointWho") String pointWho,
                       @Param("pointWhy") PointWhy pointWhy,
                       @Param("pointScore") int pointScore);

    @Update("update member set memberPoint = memberPoint + #{score} where memberId = #{memberId}")
    int updateMemberPoint(@Param("memberId") String memberId, @Param("score") int score);

    // 회원가입
    @Insert("insert into member (memberId, memberPwd, memberName, mobile, email, registerDate, memberImg, gender)\n" +
            "values (#{memberId}, #{memberPwd}, #{memberName}, #{mobile}, #{email}, #{registerDate}, #{memberImg}, #{gender})")
    int insertMember(Member member);

    //
    @Select("select * from member where memberId = #{memberId}")
    Member findById(String memberId);


}
