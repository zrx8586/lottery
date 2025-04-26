package com.example.demo.dao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

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
    private String activityName;
    private String activityDesc;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

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