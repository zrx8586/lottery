package com.example.demo.dao.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 奖品实体类
 */
@Entity
@Table(name = "lottery_prize")
@Data
public class LotteryPrize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long prizeId;

    private String prizeName;
    private String prizeDesc;
    private String prizeImageUrl;
    private String prizeCategory;
    private Double prizeValue;
    private Integer stockQuantity;
    private Boolean isActive;

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