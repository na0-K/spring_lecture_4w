package com.artineer.spring_lecture_week_2.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ApiCode {
    /* COMMON */
    SUCCESS("CM0000", "정상입니다"),
    DATA_IS_NOT_FOUND("CM0001", "데이터가 존재하지 않습니다")
    ;

    private final String name;
    private final String desc;

    ApiCode(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
