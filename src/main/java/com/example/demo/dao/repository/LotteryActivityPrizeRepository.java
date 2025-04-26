package com.example.demo.dao.repository;

import com.example.demo.dao.model.LotteryActivityPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author long_w
 */
public interface LotteryActivityPrizeRepository extends JpaRepository<LotteryActivityPrize, Long> {

    /**
     * Find all LotteryActivityPrize by activityId
     *
     * @param activityId the activityId
     * @return List of LotteryActivityPrize
     */
    @Query("SELECT p FROM LotteryActivityPrize p WHERE p.activity.activityId = :activityId")
    List<LotteryActivityPrize> findByActivityId(Long activityId);

    /**
     * 根据活动 ID 删除所有奖品关系
     * @param activityId 活动 ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM LotteryActivityPrize p WHERE p.activity.activityId = :activityId")
    void deleteByActivityId(Long activityId);
}
