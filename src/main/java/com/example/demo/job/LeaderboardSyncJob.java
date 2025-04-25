package com.example.demo.job;

import com.example.demo.model.SeasonLeaderboardRecord;
import com.example.demo.repository.SeasonLeaderboardRecordRepository;
import com.example.demo.service.LeaderboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Leaderboard 同步任务
 * @author long_w
 */
@Component
public class LeaderboardSyncJob {

    private static final Logger logger = LoggerFactory.getLogger(LeaderboardSyncJob.class);

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private SeasonLeaderboardRecordRepository recordRepository;


    @Scheduled(fixedRate = 24 * 60 * 1000) // 每小时执行一次
    public void syncLeaderboard() {
        logger.info("LeaderboardSyncJob 开始执行");

        try {
            List<Long> seasonIds = recordRepository.findAllSeasonIds();
            logger.info("获取到 {} 个赛季 ID: {}", seasonIds.size(), seasonIds);

            for (Long seasonId : seasonIds) {
                logger.info("开始同步赛季 ID: {}", seasonId);

                List<SeasonLeaderboardRecord> records = recordRepository.findBySeasonId(seasonId);
                logger.info("赛季 ID: {} 包含 {} 条记录", seasonId, records.size());

                for (SeasonLeaderboardRecord record : records) {
                    logger.info("同步记录: 赛季 ID={}, 用户 ID={}, 分数={}", seasonId, record.getUser().getUserId(), record.getScore());

                    // 更新 Redis 中的分数，而不是累加
                    leaderboardService.updateScore(seasonId, record.getUser().getUserId().toString(), record.getScore());
                }

                logger.info("完成同步赛季 ID: {}", seasonId);
            }

            logger.info("LeaderboardSyncJob 执行完成");
        } catch (Exception e) {
            logger.error("LeaderboardSyncJob 执行失败: {}", e.getMessage(), e);
        }
    }
}