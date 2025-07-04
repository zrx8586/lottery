package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.LotteryActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author long_w
 */
public interface LotteryActivityRepository extends JpaRepository<LotteryActivity, Long> {

    @Modifying
    @Query("DELETE FROM LotteryActivityPrize lap WHERE lap.activityId = :activityId")
    void deleteByActivityId(@Param("activityId") Long activityId);

}
