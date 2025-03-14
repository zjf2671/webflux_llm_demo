package com.haha.webflux.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理全局异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleException(Exception t) {
        log.error("全局异常:", t);
        return Result.error(CommonError.GLOBAL_ERROR);
    }

    /**
     * 处理自定义业务异常
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleServiceException(ServiceException e) {
        log.error("自定义业务异常：{}", e.getDescription());
        if (e.getCode() != null) {
            return Result.error(e.getCode(), e.getMessage(), e.getData());
        } else {
            return Result.error(0, e.getMessage());
        }
    }

    /**
     * 处理方法参数校验异常
     */
    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleBindException(BindException e) {
        // 从异常对象中拿到ObjectError对象
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        log.error("方法参数校验异常:" + e.getMessage(), e);
        return Result.error(CommonError.PARAMETER_ERROR.getCode(), objectError.getDefaultMessage());
    }

    /**
     * 处理url后参数读取异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        // 从异常对象中拿到ObjectError对象
        log.error("missingServletRequestParameterException:" + e.getMessage(), e);
        return Result.error(CommonError.PARAMETER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 请求体读取异常
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("httpMessageNotReadableException:" + e.getMessage(), e);
        return Result.error(CommonError.PARAMETER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 上传的文件size过大
     * @param e 异常
     * @return Result
     */
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("maxUploadSizeExceededException:" + e.getMessage(), e);
        return Result.error(CommonError.PARAMETER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 请求方法有误
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("httpRequestMethodNotSupportedException", e);
        return Result.error(CommonError.METHOD_NOT_ALLOWED);
    }

    /**
     * 客户端主动放弃
     */
    @ExceptionHandler(ClientAbortException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleClientAbortException(ClientAbortException e) {
        log.error("clientAbortException", e);
        return Result.error(0, "ClientAbort");
    }
}
