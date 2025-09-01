package com.example.demo.lottery.dao;

import java.util.List;

import lombok.Data;

@Data
public class UserSubmission {

    private List<Integer> selectedIndices;
    private int timeLeft;
    private Integer contractId; // 添加合同ID字段
    
}
