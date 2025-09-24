package com.example.demo.lottery.dto.response;

import lombok.Data;

/**
 * 抽奖资格查询响应
 */
@Data
public class EligibilityResponse {
    /** 是否有资格抽奖（活动有效且剩余次数 > 0） */
    private boolean eligible;

    /** 剩余抽奖次数（无记录时为 0） */
    private int attempts;

    /** 资格受限原因（如未开始、已结束、无资格、次数用尽等），正常为空字符串 */
    private String reason;
}


