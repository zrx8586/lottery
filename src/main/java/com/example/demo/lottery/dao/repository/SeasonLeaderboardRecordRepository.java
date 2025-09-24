package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.SeasonLeaderboardRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SeasonLeaderboardRecord 仓库接口
 * @author long_w
 */
public interface SeasonLeaderboardRecordRepository extends JpaRepository<SeasonLeaderboardRecord, Long> {

    /**
     * 查询所有的赛季 ID
     *
     * @return 赛季 ID 列表
     */
    @Query("SELECT DISTINCT r.season.seasonId FROM SeasonLeaderboardRecord r")
    List<Long> findAllSeasonIds();

    /**
     * 根据赛季 ID 查询记录
     *
     * @param seasonId 赛季 ID
     * @return 赛季排行榜记录列表
     */
    @Query("SELECT r FROM SeasonLeaderboardRecord r WHERE r.season.seasonId = :seasonId")
    List<SeasonLeaderboardRecord> findBySeasonId(Long seasonId);

    @Transactional
    @Modifying
    @Query("UPDATE SeasonLeaderboardRecord r SET r.score = :score WHERE r.season.seasonId = :seasonId AND r.user.userId = :userId")
    int updateScoreBySeasonIdAndUserId(Long seasonId, Long userId, double score);
}


