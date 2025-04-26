package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动详情 DTO
 * @author long_w
 */
@Data
public class ActivityDetailDTO {
    private Long activityId;
    private String activityName;
    private String activityDesc;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ActivityPrizeDTO> prizes;
}