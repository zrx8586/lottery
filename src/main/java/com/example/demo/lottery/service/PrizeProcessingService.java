package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.repository.LotteryActivityPrizeRepository;
import com.example.demo.lottery.dao.repository.LotteryActivityUserRepository;
import com.example.demo.lottery.dao.repository.LotteryRecordRepository;
import com.example.demo.lottery.util.JsonUtil;
import jakarta.annotation.Resource;
import jakarta.persistence.LockModeType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author long_w
 */
@Service
public class PrizeProcessingService {

    @Resource
    private LotteryActivityPrizeRepository activityPrizeRepository;

    @Resource
    private LotteryRecordRepository recordRepository;

    @Resource
    private LotteryActivityUserRepository activityUserRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void processPrize(String redisKey, LotteryActivityPrize prize, User user, LotteryActivity activity, LotteryActivityUser lotteryActivityUser) {
        // 从 Redis 中获取缓存的奖品列表 JSON 字符串
        String cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(cachedPrizesJson)) {
            throw new RuntimeException("奖品缓存不存在");
        }

        // 将 JSON 字符串反序列化为奖品列表
        List<LotteryActivityPrize> prizes = JsonUtil.fromJsonToList(cachedPrizesJson, LotteryActivityPrize.class);

        // 查找并验证奖品库存
        LotteryActivityPrize cachedPrize = null;
        for (LotteryActivityPrize p : prizes) {
            if (p.getActivityPrizeId().equals(prize.getActivityPrizeId())) {
                cachedPrize = p;
                break;
            }
        }

        if (cachedPrize == null) {
            throw new RuntimeException("奖品不存在");
        }

        // 检查库存是否充足
        if (cachedPrize.getQuantity() <= 0) {
            throw new RuntimeException("奖品库存不足");
        }

        // 获取用户抽奖次数的锁
        String userLockKey = "lock:user:" + user.getUserId() + ":attempts";
        String lockValue = String.valueOf(System.currentTimeMillis());
        boolean userLockAcquired = false;

        try {
            // 尝试获取用户抽奖次数的锁
            userLockAcquired = redisTemplate.opsForValue().setIfAbsent(userLockKey, lockValue, 10, TimeUnit.SECONDS);
            if (!userLockAcquired) {
                throw new RuntimeException("获取用户抽奖次数锁失败");
            }

            // 使用悲观锁重新获取最新的奖品数据
            LotteryActivityPrize latestPrize = activityPrizeRepository.findById(prize.getActivityPrizeId())
                    .orElseThrow(() -> new RuntimeException("奖品不存在"));

            // 检查库存
            if (latestPrize.getQuantity() <= 0) {
                throw new RuntimeException("奖品库存不足");
            }

            // 减库存
            int newQuantity = latestPrize.getQuantity() - 1;
            latestPrize.setQuantity(newQuantity);

            // 重新从数据库获取最新的用户抽奖次数
            LotteryActivityUser latestUser = activityUserRepository.findByUserIdAndActivityId(user.getUserId(), activity.getActivityId());
            if (latestUser == null || latestUser.getLotteryAttempts() <= 0) {
                throw new RuntimeException("用户抽奖次数不足");
            }

            // 更新数据库中的库存
            activityPrizeRepository.save(latestPrize);

            // 保存中奖记录
            LotteryRecord record = new LotteryRecord();
            record.setUser(user);
            record.setLotteryActivity(activity);
            record.setLotteryPrize(latestPrize.getPrize());
            recordRepository.save(record);

            // 更新用户的抽奖次数
            latestUser.setLotteryAttempts(latestUser.getLotteryAttempts() - 1);
            activityUserRepository.save(latestUser);

            // 更新 Redis 缓存
            cachedPrize.setQuantity(newQuantity);
            redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(prizes), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            // 发生异常时，回滚 Redis 缓存
            redisTemplate.opsForValue().set(redisKey, cachedPrizesJson, 10, TimeUnit.MINUTES);
            throw e;
        } finally {
            // 释放用户抽奖次数的锁
            if (userLockAcquired && lockValue.equals(redisTemplate.opsForValue().get(userLockKey))) {
                redisTemplate.delete(userLockKey);
            }
        }
    }
}