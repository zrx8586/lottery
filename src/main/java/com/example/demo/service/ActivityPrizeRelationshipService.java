package com.example.demo.service;

import com.example.demo.dto.ActivityPrizeDTO;
import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.model.LotteryPrize;
import com.example.demo.repository.LotteryActivityPrizeRepository;
import com.example.demo.repository.LotteryActivityRepository;
import com.example.demo.repository.LotteryPrizeRepository;
import com.example.demo.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityPrizeRelationshipService {

    @Autowired
    private LotteryActivityRepository activityRepository;

    @Autowired
    private LotteryActivityPrizeRepository activityPrizeRepository;

    @Autowired
    private LotteryPrizeRepository prizeRepository;

    /**
     * 获取活动及其奖品绑定关系信息
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
        List<LotteryActivityPrize> prizes = activityPrizeRepository.findByActivityId(activityId);

        // 构建 DTO
        ActivityPrizeRelationshipDTO dto = CommonUtil.buildActivityPrizeRelationshipDTO(activity);

        dto.setPrizes(CommonUtil.getActivityPrizeDTOS(prizes));

        return dto;
    }



    public void createActivityPrizeRelationship(ActivityPrizeRelationshipDTO relationshipDTO) {
        // 查询活动对象
        Optional<LotteryActivity> activityOptional = activityRepository.findById(relationshipDTO.getActivityId());
        if (activityOptional.isEmpty()) {
            throw new IllegalArgumentException("活动不存在，ID: " + relationshipDTO.getActivityId());
        }
        LotteryActivity activity = activityOptional.get();

        // 遍历奖品列表，创建绑定关系
        for (ActivityPrizeDTO prizeDTO : relationshipDTO.getPrizes()) {
            Optional<LotteryPrize> prizeOptional = prizeRepository.findById(prizeDTO.getPrizeId());
            if (prizeOptional.isEmpty()) {
                throw new IllegalArgumentException("奖品不存在，ID: " + prizeDTO.getPrizeId());
            }
            LotteryPrize prize = prizeOptional.get();

            // 创建并保存活动奖品关系
            LotteryActivityPrize activityPrize = new LotteryActivityPrize();
            activityPrize.setActivity(activity);
            activityPrize.setPrize(prize);
            activityPrize.setProbability(prizeDTO.getProbability());
            activityPrize.setQuantity(prizeDTO.getQuantity());
            activityPrizeRepository.save(activityPrize);
        }
    }

    public List<ActivityPrizeRelationshipDTO> getAll() {
        // 查询所有活动奖品关系
        List<LotteryActivityPrize> activityPrizes = activityPrizeRepository.findAll();

        // 遍历活动奖品关系列表，构建 DTO
        return activityPrizes.stream().map(activityPrize -> {
            // 获取活动信息
            LotteryActivity activity = activityPrize.getActivity();

            // 构建活动 DTO
            ActivityPrizeRelationshipDTO dto = CommonUtil.buildActivityPrizeRelationshipDTO(activity);

            // 查询当前活动的所有奖品信息
            List<LotteryActivityPrize> prizes = activityPrizeRepository.findByActivityId(activity.getActivityId());

            // 将奖品信息转换为 PrizeDTO 列表
            List<ActivityPrizeDTO> activityPrizeDTOS = CommonUtil.getActivityPrizeDTOS(prizes);

            dto.setPrizes(activityPrizeDTOS);
            return dto;
        }).distinct().toList(); // 去重，确保每个活动只返回一次
    }

}