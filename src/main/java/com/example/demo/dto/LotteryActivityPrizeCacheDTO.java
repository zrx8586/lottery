package com.example.demo.dto;

import lombok.Data;

/**
 * 奖品缓存 DTO
 * @author long_w
 */
@Data
public class LotteryActivityPrizeCacheDTO {
    private Long activityPrizeId;
    private String prizeName;
    private int quantity;
    private double probability;
}