package com.haha.webflux.common;

public enum CommonError implements ErrorMsg {

    /**
     * http请求错误
     */
    PARAMETER_ERROR(400, "请求参数有误"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed From Et."),

    /**
     * 统一系统运行时错误
     */
    GLOBAL_ERROR(2001, "系统繁忙，请稍后重试"),

    /**
     * 大模型异常统一错误
     */
    LLM_ERROR(2002, "大模型繁忙，请稍后重试~"),

    ;

    private final int code;
    private final String msg;

    CommonError(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
