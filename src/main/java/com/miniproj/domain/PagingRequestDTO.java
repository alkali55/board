package com.miniproj.domain;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Arrays;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PagingRequestDTO {

    @Builder.Default
    @Min(value = 1)
    @Positive
    private int pageNo = 1;

    @Builder.Default
    @Min(value = 10)
    @Max(value = 100)
    @Positive
    private int pagingSize = 10;

    private String link;

    private String keyword;
    private String type; // 검새타입 : c, t, w, tc, tcw

    public int getSkip(){
        return (pageNo - 1) * pagingSize;
    }

    public String getLink(){
        if (link == null){
            link = genarateLink();
        }
        return link;
    }

    private String genarateLink() {

        StringBuilder sb = new StringBuilder();

        sb.append("pageNo=").append(pageNo).append("&pagingSize=").append(pagingSize);

        if (type != null && !type.isBlank()){
            sb.append("&type=").append(type);
        }

        if(keyword != null && !keyword.isBlank()){
            sb.append("&keyword=").append(keyword);
        }
        return sb.toString();
    }

    public List<String> getSearchTypes(){

        if(type == null || type.isEmpty()){
            return null;
        }

        return Arrays.asList(type.split("")); // String[] -> List<String>
    }

    public String genarateLinkExceptPageNo() {

        StringBuilder sb = new StringBuilder();

        sb.append("pagingSize=").append(pagingSize);

        if (type != null && !type.isBlank()){
            sb.append("&type=").append(type);
        }

        if(keyword != null && !keyword.isBlank()){
            sb.append("&keyword=").append(keyword);
        }
        return sb.toString();
    }



}
