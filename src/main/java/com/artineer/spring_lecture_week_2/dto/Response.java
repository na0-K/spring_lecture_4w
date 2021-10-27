package com.artineer.spring_lecture_week_2.dto;

import com.artineer.spring_lecture_week_2.vo.ApiCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Response<T> {
    private ApiCode code;
    private T data;

    public static Response<Void> ok() {
        return Response.<Void>builder()
                .code(ApiCode.SUCCESS)
                .build();
    }
    public static <T> Response<T> ok(T data) {
        return Response.<T>builder()
                .code(ApiCode.SUCCESS)
                .data(data)
                .build();
    }
}
