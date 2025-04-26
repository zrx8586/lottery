package com.example.demo.dao.repository;

import com.example.demo.dao.model.LotteryActivityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author long_w
 */
public interface LotteryActivityUserRepository extends JpaRepository<LotteryActivityUser, Long> {
    List<LotteryActivityUser> findByActivityActivityId(Long activityId);
    List<LotteryActivityUser> findByUserUserId(Long userId);
    @Query("SELECT u FROM LotteryActivityUser u WHERE u.activity.activityId = :activityId AND u.user.userId = :userId")
    LotteryActivityUser findByUserIdAndActivityId(Long userId, Long activityId);
}