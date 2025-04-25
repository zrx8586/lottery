package com.example.demo.dto;

import lombok.Data;

@Data
public class PrizeDTO {
    private Long prizeId; // 奖品 ID
    private String prizeName; // 奖品名称
    private double probability; // 奖品概率
    private int quantity; // 奖品库存
}
