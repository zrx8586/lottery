package com.example.demo.lottery.dao.model;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @author long_w
 */
@Entity
@Table(name = "lottery_activity_user")
@Data
public class LotteryActivityUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityUserId;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private LotteryActivity activity;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime participationDate;
    private int lotteryAttempts; // 可用抽奖次数

    @Version
    private Long version;

    @Column(name = "datachange_createtime", updatable = false)
    private LocalDateTime datachangeCreateTime;

    @Column(name = "datachange_lasttime")
    private LocalDateTime datachangeLastTime;

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
