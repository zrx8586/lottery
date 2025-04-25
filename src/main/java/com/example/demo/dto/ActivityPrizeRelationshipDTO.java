package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动奖品关系 DTO
 */
@Data
public class ActivityPrizeRelationshipDTO {
    private Long activityId; // 活动 ID
    private String activityName; // 活动名称
    private String activityDesc; // 活动描述
    private LocalDateTime startDate; // 开始时间
    private LocalDateTime endDate; // 结束时间
    private List<PrizeDTO> prizes; // 奖品列表

    @Data
    public static class PrizeDTO {
        private String prizeName; // 奖品名称
        private double probability; // 奖品概率
        private int quantity; // 奖品库存
    }
}