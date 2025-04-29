package com.example.demo.lottery.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author long_w
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LotteryDrawResponse extends BaseResponse<LotteryDrawResponse> {
    private String prizeName;
    private String activityName;
}
