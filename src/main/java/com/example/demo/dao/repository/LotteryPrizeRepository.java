package com.example.demo.dao.repository;


import com.example.demo.dao.model.LotteryPrize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 奖品表的 Repository
 * @author long_w
 */
public interface LotteryPrizeRepository extends JpaRepository<LotteryPrize, Long> {
    List<LotteryPrize> findByIsActiveTrue();
    // 根据奖品 ID 查询奖品
    LotteryPrize findByPrizeId(Long prizeId);
}