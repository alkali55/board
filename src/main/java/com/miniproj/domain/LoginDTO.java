package com.miniproj.domain;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class LoginDTO {

    private String memberId;
    private String memberPwd;
    private String remember;


}
