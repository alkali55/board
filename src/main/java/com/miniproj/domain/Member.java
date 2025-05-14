package com.miniproj.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Member {

    private String memberId;
    private String memberPwd;
    private String memberName;
    private String mobile;
    private String email;
    private LocalDateTime registerDate;

    @Builder.Default
    private String memberImg = "avatar.png";

    @Builder.Default
    private int memberPoint = 100;

    @Builder.Default
    private String gender = "U";





}
