package com.example.demo.lottery.controller;

import com.example.demo.lottery.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping("/score")
    public ResponseEntity<Void> addScore(@RequestParam Long seasonId, @RequestParam String userId, @RequestParam double score) {
        leaderboardService.addScore(seasonId, userId, score);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top/{seasonId}/{topN}")
    public ResponseEntity<Set<ZSetOperations.TypedTuple<String>>> getTopPlayers(@PathVariable Long seasonId, @PathVariable int topN) {
        Set<ZSetOperations.TypedTuple<String>> topPlayers = leaderboardService.getTopPlayers(seasonId, topN);
        return ResponseEntity.ok(topPlayers);
    }

    @GetMapping("/rank/{seasonId}/{userId}")
    public ResponseEntity<Long> getRank(@PathVariable Long seasonId, @PathVariable String userId) {
        Long rank = leaderboardService.getRank(seasonId, userId);
        return ResponseEntity.ok(rank);
    }

    @GetMapping("/score/{seasonId}/{userId}")
    public ResponseEntity<Double> getScore(@PathVariable Long seasonId, @PathVariable String userId) {
        Double score = leaderboardService.getScore(seasonId, userId);
        return ResponseEntity.ok(score);
    }
}


