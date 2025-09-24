package com.example.demo.lottery.dao.model;

import lombok.Data;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_user")
@Data
public class GameUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String username;
    private String email;

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


