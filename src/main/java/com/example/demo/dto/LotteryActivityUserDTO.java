package com.example.demo.dto;

import lombok.Data;

/**
 * @author long_w
 */
@Data
public class LotteryActivityUserDTO {
    private Long activityUserId;
    private Long activityId;
    private String activityName;
    private Long userId;
    private String username;
    private int lotteryAttempts;
}
