package com.example.demo.dto;


import lombok.Data;

/**
 * 奖品 DTO，用于返回前端的奖品信息
 */
@Data
public class ActivityPrizeDTO {
    private Long prizeId;
    private String prizeName;
    private double probability;
    private int quantity;
}
