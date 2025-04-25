package com.example.demo.service;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.repository.LotteryActivityRepository;
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

        ActivityDetailDTO activityDetailDTO = new ActivityDetailDTO();
        activityDetailDTO.setActivityId(activity.getActivityId());
        activityDetailDTO.setActivityName(activity.getActivityName());
        activityDetailDTO.setActivityDesc(activity.getActivityDesc());
        activityDetailDTO.setStartDate(activity.getStartDate());
        activityDetailDTO.setEndDate(activity.getEndDate());
        activityDetailDTO.setPrizes(prizes);

        return activityDetailDTO;
    }

    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }
}