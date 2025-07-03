package com.example.demo.lottery.exception;

import lombok.Getter;

/**
 * 业务异常枚举
 * @author long_w
 */
@Getter
public enum BusinessExceptionEnum {
    // 系统级错误 500
    SYSTEM_ERROR(500, "系统内部错误"),
    
    // 活动相关错误 1001-1999
    ACTIVITY_NOT_FOUND(1001, "活动不存在"),
    ACTIVITY_NOT_STARTED(1002, "活动尚未开始"),
    ACTIVITY_ENDED(1003, "活动已结束"),
    
    // 奖品相关错误 2001-2999
    PRIZE_NOT_FOUND(2001, "奖品不存在"),
    PRIZE_STOCK_NOT_ENOUGH(2002, "奖品库存不足"),
    PRIZE_CACHE_NOT_FOUND(2003, "奖品缓存不存在"),
    
    // 用户相关错误 3001-3999
    USER_NOT_FOUND(3001, "用户不存在"),
    USER_NO_ACTIVITY_QUALIFICATION(3002, "用户没有参与活动的资格"),
    USER_NO_ATTEMPTS(3003, "用户抽奖次数已用完"),
    
    // 锁相关错误 4001-4999
    LOCK_ACQUIRE_FAILED(4001, "获取锁失败"),
    USER_LOCK_ACQUIRE_FAILED(4002, "获取用户抽奖次数锁失败");

    private final int code;
    private final String message;

    BusinessExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
} 