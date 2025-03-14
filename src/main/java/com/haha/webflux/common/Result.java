package com.haha.webflux.common;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class Result<T> {

    public static final int SUCCESS = 200;
    public static final int HALF_SUCCESS = 2000;

    @Getter
    private int code;
    @Getter
    private String msg;
    private T data;

    private Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> Result<T> ok() {
        return new Result<>(SUCCESS, null, "操作成功");
    }

    public static <T> Result<T> ok(String msg) {
        return new Result<>(SUCCESS, null, msg);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(SUCCESS, data, "操作成功");
    }

    public static <T> Result<T> ok(T data, String msg) {
        return new Result<>(SUCCESS, data, msg);
    }

    public static <T> Result<T> error(int code, String errorMsg) {
        return new Result<>(code, null, errorMsg);
    }

    public static <T> Result<T> error(int code, String errorMsg, T data) {
        return new Result<>(code, data, errorMsg);
    }

    public static <T> Result<T> error(ErrorMsg errorMsg) {
        return new Result<>(errorMsg.getCode(), null, errorMsg.getMsg());
    }

    public static <T> Result<T> error(Result<?> result) {
        return new Result<>(result.getCode(), null, result.getMsg());
    }

    public static <T> Result<T> error(ErrorMsg errorMsg, T data) {
        return new Result<>(errorMsg.getCode(), data, errorMsg.getMsg());
    }

}
