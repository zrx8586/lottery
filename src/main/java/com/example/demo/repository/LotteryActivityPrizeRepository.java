package com.example.demo.repository;

import com.example.demo.model.IdempotentRecord;
import com.example.demo.model.LotteryActivityPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
