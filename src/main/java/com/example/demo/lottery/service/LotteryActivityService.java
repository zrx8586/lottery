package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.model.ActivityStatus;
import com.example.demo.lottery.dao.repository.*;
import com.example.demo.lottery.dto.response.LotteryDrawResponse;
import com.example.demo.lottery.dto.response.LotteryRecordResponse;
import com.example.demo.lottery.exception.BusinessException;
import com.example.demo.lottery.util.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author long_w
 */
@Service
public class LotteryActivityService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryActivityService.class);

    @Resource
    private LotteryActivityUserRepository activityUserRepository;

    @Resource
    private LotteryActivityRepository activityRepository;

    @Resource
    private LotteryActivityPrizeRepository activityPrizeRepository;

    @Resource
    private LotteryRecordRepository recordRepository;

    @Resource
    private PrizeProcessingService prizeProcessingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    private Random random = new Random();

    /**
     * 获取所有活动
     * @return 活动列表
     */
    public List<LotteryActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    /**
     * 根据ID获取活动
     * @param activityId 活动ID
     * @return 活动信息
     */
    public Optional<LotteryActivity> getActivityById(Long activityId) {
        return activityRepository.findById(activityId);
    }

    /**
     * 创建新活动
     * @param activity 活动信息
     * @return 创建的活动
     */
    public LotteryActivity createActivity(LotteryActivity activity) {
        return activityRepository.save(activity);
    }

    /**
     * 删除活动
     * @param activityId 活动ID
     */
    @Transactional
    public void deleteActivity(Long activityId) {
        // 先删除活动关联的奖品关系
        activityPrizeRepository.deleteByActivityId(activityId);
        
        // 再删除活动
        activityRepository.deleteById(activityId);
    }

    // 获取活动的所有奖品
    public List<LotteryActivityPrize> getActivityPrizes(Long activityId) {
        return activityPrizeRepository.findByActivityId(activityId);
    }

    // 添加奖品到活动
    public LotteryActivityPrize addPrizeToActivity(Long activityId, LotteryActivityPrize activityPrize) {
        LotteryActivity activity = getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        activityPrize.setActivity(activity);
        return activityPrizeRepository.save(activityPrize);
    }

    // 更新活动奖品信息
    public LotteryActivityPrize updateActivityPrize(Long activityId, Long prizeId, LotteryActivityPrize activityPrize) {
        LotteryActivity activity = getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        LotteryActivityPrize existingPrize = activityPrizeRepository.findById(prizeId)
                .orElseThrow(() -> new IllegalArgumentException("奖品不存在，ID: " + prizeId));
        
        activityPrize.setActivityPrizeId(existingPrize.getActivityPrizeId());
        activityPrize.setActivity(activity);
        return activityPrizeRepository.save(activityPrize);
    }

    // 从活动中移除奖品
    public void removePrizeFromActivity(Long activityId, Long prizeId) {
        LotteryActivityPrize activityPrize = activityPrizeRepository.findById(prizeId)
                .orElseThrow(() -> new IllegalArgumentException("奖品不存在，ID: " + prizeId));
        
        if (!activityPrize.getActivity().getActivityId().equals(activityId)) {
            throw new IllegalArgumentException("奖品不属于该活动");
        }
        
        activityPrizeRepository.delete(activityPrize);
    }

    /**
     * 更新活动
     * @param activityId 活动ID
     * @param activity 更新的活动信息
     * @return 更新后的活动
     */
    public LotteryActivity updateActivity(Long activityId, LotteryActivity activity) {
        LotteryActivity existingActivity = getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        
        existingActivity.setActivityName(activity.getActivityName());
        existingActivity.setActivityDesc(activity.getActivityDesc());
        existingActivity.setStartDate(activity.getStartDate());
        existingActivity.setEndDate(activity.getEndDate());
        existingActivity.setStatus(activity.getStatus());
        
        return activityRepository.save(existingActivity);
    }
}