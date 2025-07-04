package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.LotteryActivityPrize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author long_w
 */
@Repository
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
    @Query("DELETE FROM LotteryActivityPrize lap WHERE lap.activity.activityId = :activityId")
    void deleteByActivityId(@Param("activityId") Long activityId);

    /**
     * 更新奖品库存
     * @param activityPrizeId 活动奖品ID
     * @param quantity 新的库存数量
     * @return 更新的记录数
     */
    @Modifying
    @Query("UPDATE LotteryActivityPrize p SET p.quantity = :quantity WHERE p.activityPrizeId = :activityPrizeId")
    int updateQuantity(Long activityPrizeId, int quantity);
}
