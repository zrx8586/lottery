package com.example.demo.lottery.dao.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LotteryActivityUser> lotteryActivityUsers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LotteryRecord> lotteryRecords;

    @CreationTimestamp
    @Column(name = "datachange_createtime")
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "datachange_lasttime")
    private LocalDateTime updateTime;
}
