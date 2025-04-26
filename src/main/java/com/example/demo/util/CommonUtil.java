package com.example.demo.util;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.dto.ActivityPrizeDTO;
import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.dto.LotteryActivityUserDTO;
import com.example.demo.dao.model.LotteryActivity;
import com.example.demo.dao.model.LotteryActivityPrize;
import com.example.demo.dao.model.LotteryActivityUser;

import java.util.List;

public class CommonUtil {

    /**
     * 将 LotteryActivityPrize 转换为 ActivityPrizeDTO
     * @param prizes    LotteryActivityPrize 对象的列表
     * @return        ActivityPrizeDTO 对象的列表
     */
    public static List<ActivityPrizeDTO> getActivityPrizeDTOS(List<LotteryActivityPrize> prizes) {
        return prizes.stream().map(CommonUtil::buildActivityPrizeDTO).toList();
    }

    /**
     * 将 LotteryActivityPrize 转换为 ActivityPrizeDTO
     * @param prize   LotteryActivityPrize 对象
     * @return       ActivityPrizeDTO 对象
     */
    private static ActivityPrizeDTO buildActivityPrizeDTO(LotteryActivityPrize prize) {
        ActivityPrizeDTO dto = new ActivityPrizeDTO();
        dto.setPrizeId(prize.getPrize().getPrizeId());
        dto.setPrizeName(prize.getPrize().getPrizeName());
        dto.setProbability(prize.getProbability());
        dto.setQuantity(prize.getQuantity());
        return dto;
    }

    /**
     * 构建活动详情 ActivityDetailDTO
     * @param activity LotteryActivity 对象
     * @param prizeDTOs ActivityPrizeDTO 对象的列表
     * @return ActivityDetailDTO 对象
     */
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

    /**
     * 构建活动与奖品的关系 ActivityPrizeRelationshipDTO 对象
     * @param activity LotteryActivity 对象
     * @return ActivityPrizeRelationshipDTO 对象
     */
    public static ActivityPrizeRelationshipDTO buildActivityPrizeRelationshipDTO(LotteryActivity activity) {
        ActivityPrizeRelationshipDTO dto = new ActivityPrizeRelationshipDTO();
        dto.setActivityId(activity.getActivityId());
        dto.setActivityName(activity.getActivityName());
        dto.setActivityDesc(activity.getActivityDesc());
        dto.setStartDate(TimeUtil.formatDate(activity.getStartDate(), TimeUtil.DATE_FORMAT_SHORT));
        dto.setEndDate(TimeUtil.formatDate(activity.getEndDate(), TimeUtil.DATE_FORMAT_SHORT));
        return dto;
    }

    /**
     * 构建 LotteryActivityUserDTO 对象
     * @param activityUser LotteryActivityUser 对象
     * @return LotteryActivityUserDTO 对象
     */
    public static LotteryActivityUserDTO buildLotteryActivityUserDTO(LotteryActivityUser activityUser) {
        LotteryActivityUserDTO dto = new LotteryActivityUserDTO();
        dto.setActivityUserId(activityUser.getActivityUserId());
        dto.setActivityId(activityUser.getActivity().getActivityId());
        dto.setActivityName(activityUser.getActivity().getActivityName());
        dto.setUserId(activityUser.getUser().getUserId());
        dto.setUsername(activityUser.getUser().getUsername());
        dto.setLotteryAttempts(activityUser.getLotteryAttempts());
        return dto;
    }
}
