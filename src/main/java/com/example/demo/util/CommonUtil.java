package com.example.demo.util;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.dto.ActivityPrizeDTO;
import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;

import java.util.List;

public class CommonUtil {

    /**
     * 将 LotteryActivityPrize 转换为 ActivityPrizeDTO
     * @param prizes    LotteryActivityPrize 对象的列表
     * @return        ActivityPrizeDTO 对象的列表
     */
    public static List<ActivityPrizeDTO> getActivityPrizeDTOS(List<LotteryActivityPrize> prizes) {
        return prizes.stream().map(prize -> {
            ActivityPrizeDTO dto = new ActivityPrizeDTO();
            dto.setPrizeId(prize.getPrize().getPrizeId());
            dto.setPrizeName(prize.getPrize().getPrizeName());
            dto.setProbability(prize.getProbability());
            dto.setQuantity(prize.getQuantity());
            return dto;
        }).toList();
    }

    public static ActivityDetailDTO buildActivityDetailDTO(LotteryActivity activity, List<ActivityPrizeDTO> prizeDTOs) {
        ActivityDetailDTO activityDetailDTO = new ActivityDetailDTO();
        activityDetailDTO.setActivityId(activity.getActivityId());
        activityDetailDTO.setActivityName(activity.getActivityName());
        activityDetailDTO.setActivityDesc(activity.getActivityDesc());
        activityDetailDTO.setStartDate(activity.getStartDate());
        activityDetailDTO.setEndDate(activity.getEndDate());
        activityDetailDTO.setPrizes(prizeDTOs);
        return activityDetailDTO;
    }

    public static ActivityPrizeRelationshipDTO buildActivityPrizeRelationshipDTO(LotteryActivity activity) {
        ActivityPrizeRelationshipDTO dto = new ActivityPrizeRelationshipDTO();
        dto.setActivityId(activity.getActivityId());
        dto.setActivityName(activity.getActivityName());
        dto.setActivityDesc(activity.getActivityDesc());
        dto.setStartDate(TimeUtil.formatDate(activity.getStartDate(), TimeUtil.DATE_FORMAT_SHORT));
        dto.setEndDate(TimeUtil.formatDate(activity.getEndDate(), TimeUtil.DATE_FORMAT_SHORT));
        return dto;
    }
}
