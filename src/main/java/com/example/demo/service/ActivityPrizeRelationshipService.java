package com.example.demo.service;

import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.repository.LotteryActivityPrizeRepository;
import com.example.demo.repository.LotteryActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityPrizeRelationshipService {

    @Autowired
    private LotteryActivityRepository activityRepository;

    @Autowired
    private LotteryActivityPrizeRepository prizeRepository;

    /**
     * 根据活动 ID 获取活动及其奖品信息
     * @param activityId 活动 ID
     * @return 活动奖品关系 DTO
     */
    public ActivityPrizeRelationshipDTO getActivityWithPrizes(Long activityId) {
        // 查询活动信息
        Optional<LotteryActivity> activityOptional = activityRepository.findById(activityId);
        if (activityOptional.isEmpty()) {
            throw new IllegalArgumentException("活动不存在，ID: " + activityId);
        }

        LotteryActivity activity = activityOptional.get();

        // 查询活动对应的奖品信息
        List<LotteryActivityPrize> prizes = prizeRepository.findByActivityId(activityId);

        // 构建 DTO
        ActivityPrizeRelationshipDTO dto = new ActivityPrizeRelationshipDTO();
        dto.setActivityId(activity.getActivityId());
        dto.setActivityName(activity.getActivityName());
        dto.setActivityDesc(activity.getActivityDesc());
        dto.setStartDate(activity.getStartDate());
        dto.setEndDate(activity.getEndDate());

        // 将奖品信息转换为 PrizeDTO 列表
        List<ActivityPrizeRelationshipDTO.PrizeDTO> prizeDTOs = prizes.stream().map(prize -> {
            ActivityPrizeRelationshipDTO.PrizeDTO prizeDTO = new ActivityPrizeRelationshipDTO.PrizeDTO();
            prizeDTO.setPrizeName(prize.getPrize().getPrizeName());
            prizeDTO.setProbability(prize.getProbability());
            prizeDTO.setQuantity(prize.getQuantity());
            return prizeDTO;
        }).toList();

        dto.setPrizes(prizeDTOs);

        return dto;
    }
}