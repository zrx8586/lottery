package com.example.demo.lottery.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author long_w
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {
    private int errorCode;
    private String errorMessage;

    public BusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
