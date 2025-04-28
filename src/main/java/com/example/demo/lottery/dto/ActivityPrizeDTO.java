package com.example.demo.lottery.dto;


import lombok.Data;

/**
 * 奖品 DTO，用于返回前端的奖品信息
 */
@Data
public class ActivityPrizeDTO {
    private Long prizeId; // 奖品 ID
    private String prizeName; // 奖品名称
    private double probability; // 奖品概率
    private int quantity; // 奖品库存
}
