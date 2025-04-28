package com.example.demo.lottery.dto;

import lombok.Data;

import java.util.List;

/**
 * 活动奖品关系 DTO
 */
@Data
public class ActivityPrizeRelationshipDTO {
    private Long activityId; // 活动 ID
    private String activityName; // 活动名称
    private String activityDesc; // 活动描述

    private String startDate; // 开始时间
    private String endDate; // 结束时间

    private List<ActivityPrizeDTO> prizes; // 奖品列表


}