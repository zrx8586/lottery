package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.LotteryActivityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
public interface LotteryActivityUserRepository extends JpaRepository<LotteryActivityUser, Long> {
    List<LotteryActivityUser> findByActivityActivityId(Long activityId);
    List<LotteryActivityUser> findByUserUserId(Long userId);
    @Query("SELECT u FROM LotteryActivityUser u WHERE u.activity.activityId = :activityId AND u.user.userId = :userId")
    LotteryActivityUser findByUserIdAndActivityId(Long userId, Long activityId);

    /**
     * 根据活动ID和用户ID查找用户活动关系
     *
     * @param activityId 活动ID
     * @param userId 用户ID
     * @return Optional<LotteryActivityUser>
     */
    @Query("SELECT lau FROM LotteryActivityUser lau WHERE lau.activity.activityId = :activityId AND lau.user.userId = :userId")
    Optional<LotteryActivityUser> findByActivityIdAndUserId(Long activityId, Long userId);

    /**
     * 更新用户抽奖次数
     *
     * @param activityUserId 活动用户ID
     * @param attempts 新的抽奖次数
     * @return 更新的记录数
     */
    @Modifying
    @Transactional
    @Query("UPDATE LotteryActivityUser lau SET lau.lotteryAttempts = :attempts WHERE lau.activityUserId = :activityUserId")
    int updateLotteryAttempts(Long activityUserId, int attempts);
}