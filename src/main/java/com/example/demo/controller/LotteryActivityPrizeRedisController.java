package com.example.demo.controller;

import com.example.demo.dto.request.PrizeUpdateRequest;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.service.LotteryActivityPrizeService;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 抽奖活动奖品 Redis 缓存控制器
 * 提供与奖品缓存相关的 API 接口
 */
@RestController
@RequestMapping("/api/activity-prizes/redis")
public class LotteryActivityPrizeRedisController {

    @Resource
    private LotteryActivityPrizeService prizeService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "activity:";

    /**
     * 缓存指定活动的奖品信息
     * @param activityId 活动 ID
     * @return 缓存结果
     */
    @PostMapping("/{activityId}/cache")
    public ResponseEntity<String> cachePrizes(@PathVariable Long activityId) {
        List<LotteryActivityPrize> prizes = prizeService.getPrizesByActivityId(activityId);
        String redisKey = REDIS_KEY_PREFIX + activityId + ":prizes";

        // 缓存奖品信息
        prizes.forEach(prize -> redisTemplate.opsForHash().put(
                redisKey,
                String.valueOf(prize.getPrize().getPrizeId()),
                prize
        ));

        return ResponseEntity.ok("奖品信息已缓存");
    }

    /**
     * 获取缓存中的奖品信息
     * @param activityId 活动 ID
     * @return 缓存的奖品列表
     */
    @GetMapping("/{activityId}/cache")
    public ResponseEntity<List<LotteryActivityPrize>> getCachedPrizes(@PathVariable Long activityId) {
        String redisKey = REDIS_KEY_PREFIX + activityId + ":prizes";
        List<Object> cachedPrizes = redisTemplate.opsForHash().values(redisKey);

        // 转换为 LotteryActivityPrize 对象列表
        List<LotteryActivityPrize> prizes = cachedPrizes.stream()
                .map(prize -> (LotteryActivityPrize) prize)
                .collect(Collectors.toList());

        return ResponseEntity.ok(prizes);
    }

    /**
     * 清除缓存中的奖品信息
     * @param activityId 活动 ID
     * @return 清除结果
     */
    @DeleteMapping("/{activityId}/cache")
    public ResponseEntity<String> clearCachedPrizes(@PathVariable Long activityId) {
        String redisKey = REDIS_KEY_PREFIX + activityId + ":prizes";
        redisTemplate.delete(redisKey);
        return ResponseEntity.ok("缓存已清除");
    }

    /**
     * 更新 Redis 中活动的奖品信息，并同步到数据库
     * @param activityId 活动 ID
     * @param updates 奖品更新请求列表
     * @return 更新结果
     */
    @PutMapping("/{activityId}/update")
    public ResponseEntity<String> updateCachedPrizes(
            @PathVariable Long activityId,
            @RequestBody List<PrizeUpdateRequest> updates) {
        String redisKey = REDIS_KEY_PREFIX + activityId + ":prizes";

        // 从 Redis 中读取奖品信息
        List<Object> cachedPrizes = redisTemplate.opsForHash().values(redisKey);

        if (cachedPrizes.isEmpty()) {
            // 如果 Redis 中没有数据，从数据库加载并缓存
            List<LotteryActivityPrize> prizes = prizeService.getPrizesByActivityId(activityId);
            prizes.forEach(prize -> redisTemplate.opsForHash().put(
                    redisKey,
                    String.valueOf(prize.getPrize().getPrizeId()),
                    prize
            ));
            cachedPrizes = redisTemplate.opsForHash().values(redisKey);
        }

        // 更新 Redis 和数据库中的奖品信息
        for (PrizeUpdateRequest update : updates) {
            LotteryActivityPrize prizeToUpdate = cachedPrizes.stream()
                    .map(prize -> (LotteryActivityPrize) prize)
                    .filter(prize -> prize.getPrize().getPrizeId().equals(update.getPrizeId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("奖品不存在，ID: " + update.getPrizeId()));

            // 更新 Redis 中的奖品信息
            prizeToUpdate.setProbability(update.getProbability());
            prizeToUpdate.setQuantity(update.getQuantity());
            redisTemplate.opsForHash().put(redisKey, String.valueOf(prizeToUpdate.getPrize().getPrizeId()), prizeToUpdate);

            // 同步更新数据库
            prizeService.updatePrizes(activityId, List.of(update));
        }

        return ResponseEntity.ok("奖品信息已更新并同步到数据库");
    }
}