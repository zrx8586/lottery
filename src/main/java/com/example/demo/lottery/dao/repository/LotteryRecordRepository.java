package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.LotteryRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author long_w
 */
public interface LotteryRecordRepository extends JpaRepository<LotteryRecord, Long> {

    // 查询所有中奖记录
    List<LotteryRecord> findAll();

    // 根据用户ID查询中奖记录
    @Query("SELECT r FROM LotteryRecord r WHERE r.lotteryUser.userId = :userId")
    List<LotteryRecord> findByUserId(Long userId);

    @Query("SELECT r FROM LotteryRecord r WHERE r.lotteryActivity.activityId = :activityId")
    List<LotteryRecord> findByActivityId(@Param("activityId") Long activityId);

    // 根据用户ID和活动ID查询中奖记录
    @Query("SELECT r FROM LotteryRecord r WHERE r.lotteryUser.userId = :userId AND r.lotteryActivity.activityId = :activityId")
    List<LotteryRecord> findByUserIdAndActivityId(Long userId, Long activityId);
}
