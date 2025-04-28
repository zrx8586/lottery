package com.example.demo.lottery.dao.repository;

import com.example.demo.lottery.dao.model.LotteryActivity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author long_w
 */
public interface LotteryActivityRepository extends JpaRepository<LotteryActivity, Long> {
}
