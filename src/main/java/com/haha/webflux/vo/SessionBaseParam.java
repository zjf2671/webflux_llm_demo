package com.haha.webflux.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SessionBaseParam {

    @NotNull(message = "会话id不能为空")
    private String chatSessionId;

}
