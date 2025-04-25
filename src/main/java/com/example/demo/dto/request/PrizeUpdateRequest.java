package com.example.demo.dto.request;

import lombok.Data;

/**
 * 奖品更新请求 DTO
 */
@Data
public class PrizeUpdateRequest {
    private Long prizeId;
    private double probability;
    private int quantity;
}