package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.LotteryActivity;
import com.example.demo.lottery.dao.repository.LotteryActivityRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@Service
public class LotteryActivityService {

    @Resource
    private LotteryActivityRepository activityRepository;

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

    // 更新活动信息
    public void deleteActivity(Long activityId) {
        activityRepository.deleteById(activityId);
    }

    /**
     * 更新活动状态
     * 根据当前时间和活动的开始/结束时间自动更新状态
     */
    public void updateActivityStatus(LotteryActivity activity) {
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isBefore(activity.getStartDate())) {
            activity.setStatus("PENDING");
        } else if (now.isAfter(activity.getEndDate())) {
            activity.setStatus("ENDED");
        } else {
            activity.setStatus("ACTIVE");
        }
        
        activityRepository.save(activity);
    }
}