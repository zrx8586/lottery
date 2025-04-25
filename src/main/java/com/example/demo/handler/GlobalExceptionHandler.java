package com.example.demo.handler;

import com.example.demo.dto.response.BaseResponse;
import com.example.demo.exception.BusinessException;
import io.swagger.v3.oas.annotations.Hidden;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 全局异常处理器
 * @author long_w
 */
@Hidden
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.error("IllegalArgumentException: {}", e.getMessage(), e);
        return buildResponseEntity(BaseResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        logger.error("IllegalStateException: {}", e.getMessage(), e);
        return buildResponseEntity(BaseResponse.failure(e.getMessage(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception e) {
        logger.error("Unhandled Exception: {}", e.getMessage(), e);
        return buildResponseEntity(BaseResponse.failure("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Void>> handleBusinessException(BusinessException e) {
        logger.error("BusinessException: {}", e.getErrorMessage(), e);
        return buildResponseEntity(BaseResponse.failure(e.getErrorMessage(), e.getErrorCode()), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BaseResponse<Void>> buildResponseEntity(BaseResponse<Void> response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }
}