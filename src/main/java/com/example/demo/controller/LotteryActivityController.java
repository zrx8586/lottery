package com.example.demo.controller;

import com.example.demo.model.LotteryActivity;
import com.example.demo.service.LotteryActivityService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author long_w
 */
@RestController
@RequestMapping("/api/activity")
public class LotteryActivityController {

    @Resource
    private LotteryActivityService activityService;

    /**
     * @return 所有活动列表
     */
    // 查询所有活动
    @GetMapping("/all")
    public ResponseEntity<List<LotteryActivity>> getAllActivities() {
        List<LotteryActivity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    /**
     * 创建活动
     * @param activity 活动信息
     * @return 创建的活动信息
     */
    // 创建新活动
    @PostMapping("/create")
    public ResponseEntity<LotteryActivity> createActivity(@RequestBody LotteryActivity activity) {
        LotteryActivity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdActivity);
    }

    /**
     * 更新活动信息
     * @param activityId 活动ID
     * @param updatedActivity 更新的活动信息
     * @return 更新后的活动信息
     */
    // 更新活动
    @PutMapping("/{activityId}")
    public ResponseEntity<LotteryActivity> updateActivity(
            @PathVariable Long activityId,
            @RequestBody LotteryActivity updatedActivity) {
        LotteryActivity existingActivity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        updatedActivity.setActivityId(existingActivity.getActivityId());
        LotteryActivity savedActivity = activityService.createActivity(updatedActivity);
        return ResponseEntity.ok(savedActivity);
    }


    /**
     * 删除活动
     * @param activityId 活动ID
     * @return 删除结果
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long activityId) {
        activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        activityService.deleteActivity(activityId);
        return ResponseEntity.ok("活动已成功删除");
    }
}