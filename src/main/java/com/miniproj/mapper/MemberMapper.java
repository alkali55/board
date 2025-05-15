package com.miniproj.mapper;

import com.miniproj.domain.AutoLoginInfo;
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

    // 자동로그인 정보 저장
    @Update("update member set sesId = #{sesId}, allimit = #{allimit} where memberId = #{memberId}")
    int updateAutoLoginInfo(AutoLoginInfo autoLoginInfo);

    // 자동로그인 유저를 확인
    @Select("select * from member where sesId = #{sesId} and allimit > now()")
    Member checkAutoLoginMember(String sesId);

    @Update("update member set sesId = null, allimit = null where memberId = #{memberId}")
    void clearAutoLoginInfo(String memberId);
}
