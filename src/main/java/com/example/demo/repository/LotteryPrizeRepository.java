package com.example.demo.repository;


import com.example.demo.model.LotteryPrize;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author long_w
 */
public interface LotteryPrizeRepository extends JpaRepository<LotteryPrize, Long> {
}