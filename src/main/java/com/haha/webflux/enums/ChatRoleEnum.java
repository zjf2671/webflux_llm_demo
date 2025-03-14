package com.haha.webflux.enums;

import lombok.Getter;

/**
 * 聊天角色枚举
 * @author lenovo
 */
@Getter
public enum ChatRoleEnum {

    system("系统消息"),
    user("用户消息"),
    assistant("对话助手消息"),
    function("函数"),
    tool("工具调用消息");

    private final String desc;

    ChatRoleEnum(String desc) {
        this.desc = desc;
    }
}
