package com.example.demo.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author long_w
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LotteryDrawResponse extends BaseResponse {
    private String prizeName;
    private String activityName;
}
