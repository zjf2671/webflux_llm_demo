package com.haha.webflux.common;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ServiceException extends RuntimeException {

    private Integer code;
    private String description;
    private List<String> args = new ArrayList<>();
    private Object data;

    public ServiceException() {
    }

    public ServiceException(String description) {
        super(description);
        this.description = description;
    }

    public ServiceException(String description, Object data) {
        super(description);
        this.description = description;
        this.data = data;
    }

    public ServiceException(ErrorMsg errorMsg) {
        this(errorMsg.getCode(), errorMsg.getMsg());
    }

    public ServiceException(CommonError errorCode, String... args) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = errorCode.getMsg();
        this.args = Arrays.asList(args);
    }

    public ServiceException(CommonError errorCode, String msg) {
        super(msg);
        this.code = errorCode.getCode();
        this.description = msg;
    }

    public ServiceException(CommonError errorCode, String msg, Exception cause) {
        super(msg, cause);
        this.code = errorCode.getCode();
        this.description = msg;
    }

    public ServiceException(CommonError errorCode, Object data) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
        this.description = errorCode.getMsg();
        this.data = data;
    }

    public ServiceException(int code, String description) {
        super(description);
        this.description = description;
        this.code = code;
    }

    public ServiceException(int code, Throwable cause) {
        this(code, cause.getMessage());
    }

    public Integer getCode() {
        return null == code ? CommonError.GLOBAL_ERROR.getCode() : code;
    }
}
