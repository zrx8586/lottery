package com.example.demo.lottery.dao.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @author long_w
 */
@Entity
@Table(name = "season")
@Data
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seasonId;
    private String seasonName;
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


