package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class LotteryRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 外键列名为 user_id
    private LotteryUser lotteryUser;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private LotteryActivity lotteryActivity;

    @ManyToOne
    @JoinColumn(name = "prize_id")
    private LotteryPrize lotteryPrize;

    private LocalDateTime wonAt = LocalDateTime.now();

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
