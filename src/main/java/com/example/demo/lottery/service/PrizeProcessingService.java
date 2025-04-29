package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.repository.LotteryActivityPrizeRepository;
import com.example.demo.lottery.dao.repository.LotteryActivityUserRepository;
import com.example.demo.lottery.dao.repository.LotteryRecordRepository;
import com.example.demo.lottery.util.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

        // 更新对应奖品的库存
        for (LotteryActivityPrize cachedPrize : prizes) {
            if (cachedPrize.getActivityPrizeId().equals(prize.getActivityPrizeId())) {
                // 减库存
                int newQuantity = cachedPrize.getQuantity() - 1;
                // 更新 Redis 中的奖品信息
                cachedPrize.setQuantity(newQuantity);
                // 更新奖品对象
                prize.setQuantity(newQuantity);
                break;
            }
        }

        // 将更新后的奖品列表序列化为 JSON 字符串并存回 Redis
        redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(prizes), 10, TimeUnit.MINUTES);

        // 更新数据库中的库存
        activityPrizeRepository.save(prize);

        // 保存中奖记录
        LotteryRecord record = new LotteryRecord();
        record.setUser(user);
        record.setLotteryActivity(activity);
        record.setLotteryPrize(prize.getPrize());
        recordRepository.save(record);

        // 更新用户的抽奖次数
        lotteryActivityUser.setLotteryAttempts(lotteryActivityUser.getLotteryAttempts() - 1);
        activityUserRepository.save(lotteryActivityUser);
    }
}