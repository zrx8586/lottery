package com.example.demo.lottery.controller;

import com.example.demo.lottery.dao.model.LotteryActivity;
import com.example.demo.lottery.dao.model.ActivityStatus;
import com.example.demo.lottery.service.LotteryActivityService;
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
        // 设置初始状态
        activity.setStatus(ActivityStatus.INACTIVE);
        LotteryActivity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdActivity);
    }

    /**
     * 更新活动信息
     * @param activityId 活动ID
     * @param activity 更新的活动信息
     * @return 更新后的活动信息
     */
    // 更新活动
    @PutMapping("/{activityId}")
    public ResponseEntity<LotteryActivity> updateActivity(@PathVariable Long activityId, @RequestBody LotteryActivity activity) {
        LotteryActivity existingActivity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        
        existingActivity.setName(activity.getName());
        existingActivity.setDescription(activity.getDescription());
        existingActivity.setStartDate(activity.getStartDate());
        existingActivity.setEndDate(activity.getEndDate());
        
        return ResponseEntity.ok(activityService.createActivity(existingActivity));
    }

    /**
     * 更新活动状态
     * @param activityId 活动ID
     * @param status 新状态
     * @return 更新后的活动信息
     */
    @PutMapping("/{activityId}/status")
    public ResponseEntity<LotteryActivity> updateActivityStatus(@PathVariable Long activityId, @RequestParam ActivityStatus status) {
        LotteryActivity existingActivity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        
        existingActivity.setStatus(status);
        return ResponseEntity.ok(activityService.createActivity(existingActivity));
    }

    /**
     * 删除活动
     * @param activityId 活动ID
     * @return 删除结果
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long activityId) {
        activityService.deleteActivity(activityId);
        return ResponseEntity.noContent().build();
    }
}