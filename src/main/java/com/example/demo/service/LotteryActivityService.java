package com.example.demo.service;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.dto.ActivityPrizeDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.repository.LotteryActivityRepository;
import com.example.demo.util.CommonUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@Service
public class LotteryActivityService {

    @Resource
    private LotteryActivityRepository activityRepository;

    @Resource
    private LotteryActivityPrizeService prizeService;

    // 查询所有活动
    public List<LotteryActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    // 根据活动 ID 查询活动
    public Optional<LotteryActivity> getActivityById(Long activityId) {
        return activityRepository.findById(activityId);
    }

    // 创建新活动
    public LotteryActivity createActivity(LotteryActivity activity) {
        return activityRepository.save(activity);
    }


    // 获取活动及其奖品信息
    public ActivityDetailDTO getActivityWithPrizes(Long activityId) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));

        List<LotteryActivityPrize> prizes = prizeService.getPrizesByActivityId(activityId);

        // 将 LotteryActivityPrize 转换为 ActivityPrizeDTO
        List<ActivityPrizeDTO> prizeDTOs =  CommonUtil.getActivityPrizeDTOS(prizes);

        return CommonUtil.buildActivityDetailDTO(activity, prizeDTOs);
    }



    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }
}