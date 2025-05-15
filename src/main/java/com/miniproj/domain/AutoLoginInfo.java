package com.miniproj.domain;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class AutoLoginInfo {

    private String memberId;
    private String sesId;
    private LocalDateTime allimit;
}
