package com.example.demo.repository;


import com.example.demo.model.LotteryPrize;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 奖品表的 Repository
 * @author long_w
 */
public interface LotteryPrizeRepository extends JpaRepository<LotteryPrize, Long> {
}