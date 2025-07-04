package com.example.demo.lottery.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author long_w
 */
@Entity
@Table(name = "lottery_activity_prize")
@Data
public class LotteryActivityPrize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityPrizeId;

    @Column(name = "activity_id")
    private Long activityId; // 添加这个字段

    @ManyToOne
    @JoinColumn(name = "activity_id", insertable = false, updatable = false)
    private LotteryActivity activity;

    @ManyToOne
    @JoinColumn(name = "prize_id")
    private LotteryPrize prize;

    private double probability;

    private int quantity;

    @Column(name = "datachange_createtime", updatable = false)
    private LocalDateTime datachangeCreateTime;

    @Column(name = "datachange_lasttime")
    private LocalDateTime datachangeLastTime;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        datachangeCreateTime = LocalDateTime.now();
        datachangeLastTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        datachangeLastTime = LocalDateTime.now();
    }
}