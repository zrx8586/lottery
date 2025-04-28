package com.example.demo.lottery.util;

/**
 * @author long_w
 */
public class RedisKeyUtil {

    private static final String LEADERBOARD_KEY_FORMAT = "game:leaderboard:%d";

    public static String getLeaderboardKey(Long seasonId) {
        return String.format(LEADERBOARD_KEY_FORMAT, seasonId);
    }
}