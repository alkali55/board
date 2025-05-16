package com.miniproj.domain;


import lombok.Builder;
import lombok.Getter;

@Getter
public class MyResponseWithData<T> {

    private int resultCode;
    private String resultMessage;
    private T data;

    @Builder
    public MyResponseWithData(ResponseType responseType, T data) {
        resultCode = responseType.getResultCode();
        resultMessage = responseType.getResultMessage();
        this.data = data;
    }

    /**
     * data 없이 성공했다는 코드(200)와 "SUCCESS"만 전송
     *
     */

    public static MyResponseWithData success() {
        return MyResponseWithData.builder()
                .responseType(ResponseType.SUCCESS)
                .build();
    }

    /**
     * data와 함께 성공했다는 코드(200)와 "SUCCESS" 전송
     * @param data
     * @return
     * @param <T>
     */
    
    public static <T> MyResponseWithData<T> success(T data) {
        return new MyResponseWithData<>(ResponseType.SUCCESS, data);
    }

    /**
     * 실패했다는 코드(400)와 "FAIL"만 전송
     * @return MyResponseWithData
     */
    public static MyResponseWithData fail(){
        return MyResponseWithData.builder()
                .responseType(ResponseType.FAIL)
                .build();
    }
}
