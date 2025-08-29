package com.example.demo.lottery.dao.model;

import lombok.Data;

@Data
public class GameResult {
    private int correctCount;
    private int totalErrors;
    private int score;
    private String message;
}
