package com.example.demo.lottery.dao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author long_w
 */
@Entity
@Table(name = "lottery_activity")
@Data
public class LotteryActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId;

    @Column(nullable = false)
    private String activityName;

    @Column
    private String activityDesc;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus status;

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