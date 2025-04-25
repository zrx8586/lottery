package com.example.demo.dto.request;

import lombok.Data;

/**
 * @author long_w
 * 支付请求数据传输对象
 */
@Data
public class ProcessPaymentRequest {
    private Long orderId;          // 订单ID
    private String idempotencyKey; // 幂等键
    private double amount;         // 支付金额
}
