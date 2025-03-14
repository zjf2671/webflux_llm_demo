package com.haha.webflux.enums;

import lombok.Getter;

/**
 * 聊天角色枚举
 * @author lenovo
 */
@Getter
public enum EStreamTypeEnum {

    think("思考中..."),
    data("数据整理中..."),
    text("正文内容"),
    meta("元数据"),
    ;

    private final String desc;

    EStreamTypeEnum(String desc) {
        this.desc = desc;
    }
}
