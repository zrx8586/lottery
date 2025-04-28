package com.example.demo.lottery.common;

import lombok.Getter;

/**
 * @author long_w
 */

@Getter
public enum PaymentStatusEnum {
    PROCESSING(0, "处理中"),
    SUCCESS(1, "成功"),
    FAILED(2, "失败");

    private final int code;
    private final String description;

    PaymentStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

}
