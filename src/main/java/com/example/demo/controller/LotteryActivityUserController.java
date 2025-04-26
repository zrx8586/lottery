package com.example.demo.controller;

import com.example.demo.dto.LotteryActivityUserDTO;
import com.example.demo.dao.model.LotteryActivityUser;
import com.example.demo.service.LotteryActivityUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-users")
public class LotteryActivityUserController {

    @Autowired
    private LotteryActivityUserService activityUserService;

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<LotteryActivityUserDTO>> getUsersByActivityId(@PathVariable Long activityId) {
        List<LotteryActivityUserDTO> users = activityUserService.getUsersByActivityId(activityId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LotteryActivityUserDTO>> getActivitiesByUserId(@PathVariable Long userId) {
        List<LotteryActivityUserDTO> activities = activityUserService.getActivitiesByUserId(userId);
        return ResponseEntity.ok(activities);
    }

    @PostMapping
    public ResponseEntity<LotteryActivityUser> saveActivityUser(@RequestBody LotteryActivityUser activityUser) {
        LotteryActivityUser savedActivityUser = activityUserService.saveActivityUser(activityUser);
        return ResponseEntity.ok(savedActivityUser);
    }

    @PutMapping("/{activityUserId}/attempts")
    public ResponseEntity<Void> updateLotteryAttempts(@PathVariable Long activityUserId, @RequestParam int attempts) {
        activityUserService.updateLotteryAttempts(activityUserId, attempts);
        return ResponseEntity.noContent().build();
    }
}
