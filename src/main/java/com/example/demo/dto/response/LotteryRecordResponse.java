package com.example.demo.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author long_w
 */
@Data
public class LotteryRecordResponse {
    private String username;
    private String activityName;
    private String prizeName;
    private LocalDateTime wonAt;
}