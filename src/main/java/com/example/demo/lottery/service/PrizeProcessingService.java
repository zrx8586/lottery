package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.repository.LotteryActivityPrizeRepository;
import com.example.demo.lottery.dao.repository.LotteryActivityUserRepository;
import com.example.demo.lottery.dao.repository.LotteryRecordRepository;
import com.example.demo.lottery.exception.BusinessException;
import com.example.demo.lottery.exception.BusinessExceptionEnum;
import com.example.demo.lottery.util.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 奖品处理服务
 * @author long_w
 */
@Service
public class PrizeProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(PrizeProcessingService.class);
    private static final int LOCK_TIMEOUT_SECONDS = 10;
    private static final int CACHE_TIMEOUT_MINUTES = 10;

    @Resource
    private LotteryActivityPrizeRepository activityPrizeRepository;

    @Resource
    private LotteryRecordRepository recordRepository;

    @Resource
    private LotteryActivityUserRepository activityUserRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 处理奖品
     * @param redisKey Redis缓存的key
     * @param prize 奖品
     * @param user 用户
     * @param activity 活动
     * @param lotteryActivityUser 用户活动关联
     */
    @Transactional(rollbackFor = Exception.class)
    public void processPrize(String redisKey, LotteryActivityPrize prize, User user,
                             LotteryActivity activity, LotteryActivityUser lotteryActivityUser) {
        logger.info("开始处理奖品: 用户={}, 活动={}, 奖品={}",
            user.getUsername(), activity.getActivityName(), prize.getPrize().getPrizeName());

        // 获取并验证缓存
        List<LotteryActivityPrize> prizes = getAndValidateCachedPrizes(redisKey, prize.getActivityPrizeId());

        // 获取用户抽奖次数锁
        String userLockKey = getLockKey("user", user.getUserId(), "attempts");
        String lockValue = String.valueOf(System.currentTimeMillis());

        try {
            // 获取锁
            if (!acquireLock(userLockKey, lockValue)) {
                throw new BusinessException(BusinessExceptionEnum.USER_LOCK_ACQUIRE_FAILED);
            }

            // 处理奖品
            processPrizeWithLock(redisKey, prize, user, activity, lotteryActivityUser, prizes);

        } catch (BusinessException e) {
            logger.warn("处理奖品业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("处理奖品系统异常: {}", e.getMessage(), e);
            throw new BusinessException(BusinessExceptionEnum.SYSTEM_ERROR, e);
        } finally {
            // 释放锁
            releaseLock(userLockKey, lockValue);
        }
    }

    /**
     * 获取并验证缓存的奖品列表
     */
    private List<LotteryActivityPrize> getAndValidateCachedPrizes(String redisKey, Long prizeId) {
        String cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(cachedPrizesJson)) {
            throw new BusinessException(BusinessExceptionEnum.PRIZE_CACHE_NOT_FOUND);
        }

        List<LotteryActivityPrize> prizes = JsonUtil.fromJsonToList(cachedPrizesJson, LotteryActivityPrize.class);
        LotteryActivityPrize cachedPrize = prizes.stream()
            .filter(p -> p.getActivityPrizeId().equals(prizeId))
            .findFirst()
            .orElseThrow(() -> new BusinessException(BusinessExceptionEnum.PRIZE_NOT_FOUND));

        if (cachedPrize.getQuantity() <= 0) {
            throw new BusinessException(BusinessExceptionEnum.PRIZE_STOCK_NOT_ENOUGH);
        }

        return prizes;
    }

    /**
     * 使用锁处理奖品
     */
    private void processPrizeWithLock(String redisKey, LotteryActivityPrize prize, User user,
                                      LotteryActivity activity, LotteryActivityUser lotteryActivityUser,
                                      List<LotteryActivityPrize> prizes) {
        // 使用悲观锁重新获取最新的奖品数据
        LotteryActivityPrize latestPrize = activityPrizeRepository.findById(prize.getActivityPrizeId())
            .orElseThrow(() -> new BusinessException(BusinessExceptionEnum.PRIZE_NOT_FOUND));

        // 检查库存
        if (latestPrize.getQuantity() <= 0) {
            throw new BusinessException(BusinessExceptionEnum.PRIZE_STOCK_NOT_ENOUGH);
        }

        // 重新从数据库获取最新的用户抽奖次数
        LotteryActivityUser latestUser = activityUserRepository.findByUserIdAndActivityId(
            user.getUserId(), activity.getActivityId());
        if (latestUser == null || latestUser.getLotteryAttempts() <= 0) {
            throw new BusinessException(BusinessExceptionEnum.USER_NO_ATTEMPTS);
        }

        // 减库存
        int newQuantity = latestPrize.getQuantity() - 1;
        latestPrize.setQuantity(newQuantity);
        activityPrizeRepository.save(latestPrize);

        // 保存中奖记录
        saveLotteryRecord(user, activity, latestPrize);

        // 更新用户的抽奖次数
        updateUserAttempts(latestUser);

        // 更新缓存
        updatePrizeCache(redisKey, prizes, latestPrize);
    }

    /**
     * 保存中奖记录
     */
    private void saveLotteryRecord(User user, LotteryActivity activity, LotteryActivityPrize prize) {
        LotteryRecord record = new LotteryRecord();
        record.setUser(user);
        record.setLotteryActivity(activity);
        record.setLotteryPrize(prize.getPrize());
        recordRepository.save(record);
        logger.info("保存中奖记录成功: 用户={}, 奖品={}", user.getUsername(), prize.getPrize().getPrizeName());
    }

    /**
     * 更新用户抽奖次数
     */
    private void updateUserAttempts(LotteryActivityUser user) {
        user.setLotteryAttempts(user.getLotteryAttempts() - 1);
        activityUserRepository.save(user);
        logger.info("更新用户抽奖次数成功: 用户ID={}, 剩余次数={}", user.getUser().getUserId(), user.getLotteryAttempts());
    }

    /**
     * 更新奖品缓存
     */
    private void updatePrizeCache(String redisKey, List<LotteryActivityPrize> prizes, LotteryActivityPrize updatedPrize) {
        prizes.stream()
            .filter(p -> p.getActivityPrizeId().equals(updatedPrize.getActivityPrizeId()))
            .findFirst()
            .ifPresent(p -> p.setQuantity(updatedPrize.getQuantity()));

        redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(prizes), CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        logger.info("更新奖品缓存成功: 奖品ID={}, 新库存={}", updatedPrize.getActivityPrizeId(), updatedPrize.getQuantity());
    }

    /**
     * 获取锁
     */
    private boolean acquireLock(String lockKey, String lockValue) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     */
    private void releaseLock(String lockKey, String lockValue) {
        if (lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
            redisTemplate.delete(lockKey);
            logger.debug("释放锁成功: {}", lockKey);
        }
    }

    /**
     * 生成锁的key
     */
    private String getLockKey(String type, Long id, String suffix) {
        return String.format("lock:%s:%d:%s", type, id, suffix);
    }
}
