package com.haha.webflux.enums;

import lombok.Getter;

/**
 * 流式问答枚举
 */
@Getter
public enum FlowChatSceneEnum {

    BASE_CHAT("主对话"),
    FOLLOW_UP_CHAT("追问对话"),
    GENERATE_DOC_SUMMARY("生成文档摘要");

    // ...

    private final String desc;

    FlowChatSceneEnum(String desc) {
        this.desc = desc;
    }
}
