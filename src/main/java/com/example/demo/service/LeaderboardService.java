package com.example.demo.service;

import com.example.demo.dao.repository.SeasonLeaderboardRecordRepository;
import com.example.demo.util.RedisKeyUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author long_w
 */
@Service
public class LeaderboardService {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardService.class);

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> zSetOps;

    @Autowired
    private SeasonLeaderboardRecordRepository recordRepository;

    @PostConstruct
    public void init() {
        this.zSetOps = redisTemplate.opsForZSet();
    }

    private String getLeaderboardKey(Long seasonId) {
        return RedisKeyUtil.getLeaderboardKey(seasonId);
    }

    public void addScore(Long seasonId, String userId, double score) {
        try {
            // 基于当前 Redis 分数累加
            Double updatedScore = zSetOps.incrementScore(getLeaderboardKey(seasonId), userId, score);

            if (updatedScore != null) {
                try {
                    // 同步更新 MySQL 中的分数
                    int updatedRows = recordRepository.updateScoreBySeasonIdAndUserId(seasonId, Long.parseLong(userId), updatedScore);
                    if (updatedRows == 0) {
                        logger.warn("No record updated in MySQL for seasonId={}, userId={}", seasonId, userId);
                    }
                } catch (Exception e) {
                    // 如果 MySQL 更新失败，回滚 Redis 的分数累加
                    zSetOps.incrementScore(getLeaderboardKey(seasonId), userId, -score);
                    logger.error("MySQL 更新失败，已回滚 Redis 分数: seasonId={}, userId={}, score={}", seasonId, userId, score);
                    throw e; // 继续抛出异常
                }
            } else {
                logger.error("Failed to increment score in Redis for seasonId={}, userId={}", seasonId, userId);
            }
        } catch (Exception e) {
            logger.error("Failed to add score for seasonId={}, userId={}, score={}: {}", seasonId, userId, score, e.getMessage());
        }
    }

    public Set<ZSetOperations.TypedTuple<String>> getTopPlayers(Long seasonId, int topN) {
        try {
            return zSetOps.reverseRangeWithScores(getLeaderboardKey(seasonId), 0, topN - 1);
        } catch (Exception e) {
            logger.error("Failed to get top players for seasonId={}, topN={}: {}", seasonId, topN, e.getMessage());
            return Set.of(); // 返回空集合
        }
    }

    public Long getRank(Long seasonId, String userId) {
        try {
            Long rank = zSetOps.reverseRank(getLeaderboardKey(seasonId), userId);
            return (rank != null) ? rank + 1 : -1; // 如果用户未上榜，返回 -1
        } catch (Exception e) {
            logger.error("Failed to get rank for seasonId={}, userId={}: {}", seasonId, userId, e.getMessage());
            return -1L;
        }
    }

    public Double getScore(Long seasonId, String userId) {
        try {
            return zSetOps.score(getLeaderboardKey(seasonId), userId);
        } catch (Exception e) {
            logger.error("Failed to get score for seasonId={}, userId={}: {}", seasonId, userId, e.getMessage());
            return null;
        }
    }

    public void updateScore(Long seasonId, String userId, double score) {
        try {
            String leaderboardKey = getLeaderboardKey(seasonId);
            // 使用 ZADD 的 XX 参数更新分数
            zSetOps.add(leaderboardKey, userId, score);
        } catch (Exception e) {
            logger.error("Failed to update score for seasonId={}, userId={}, score={}: {}", seasonId, userId, score, e.getMessage());
        }
    }
}