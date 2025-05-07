package com.example.demo.lottery.exception;

import lombok.Getter;

/**
 * 业务异常
 * @author long_w
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;

    public BusinessException(BusinessExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }

    public BusinessException(BusinessExceptionEnum exceptionEnum, String message) {
        super(message);
        this.code = exceptionEnum.getCode();
        this.message = message;
    }

    public BusinessException(BusinessExceptionEnum exceptionEnum, Throwable cause) {
        super(exceptionEnum.getMessage(), cause);
        this.code = exceptionEnum.getCode();
        this.message = exceptionEnum.getMessage();
    }
}
