package com.haha.webflux.enums;

import lombok.Getter;

/**
 * 文案icon类型枚举
 * @author lenovo
 */
@Getter
public enum EsIconTypeEnum {

    /**
     * 思考中...
     */
    one(1, "思考中..."),
    /**
     * 已深度思考（用时7秒）
     */
    two(2, "已深度思考%s秒"),
    /**
     * 数据整理中...
     */
    three(3, "数据整理中..."),
    /**
     * 数据详情
     */
    four(4, "数据详情"),

    ;
    private final Integer iconIndex;

    private final String desc;

    EsIconTypeEnum(Integer number, String desc) {
        this.iconIndex = number;
        this.desc = desc;
    }
}
