package com.example.demo.controller;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.service.LotteryActivityPrizeService;
import com.example.demo.service.LotteryActivityService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@RestController
@RequestMapping("/api/activity")
public class LotteryActivityController {

    @Resource
    private LotteryActivityService activityService;

    @Resource
    private LotteryActivityPrizeService prizeService;

    // 创建新活动
    @PostMapping("/create")
    public ResponseEntity<LotteryActivity> createActivity(@RequestBody LotteryActivity activity) {
        LotteryActivity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdActivity);
    }

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

    // 根据活动 ID 查询活动详情（包含奖品信息）
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDetailDTO> getActivityById(@PathVariable Long activityId) {
        ActivityDetailDTO activityDetailDTO = activityService.getActivityWithPrizes(activityId);
        return ResponseEntity.ok(activityDetailDTO);
    }

    // 查询所有活动
    @GetMapping("/all")
    public ResponseEntity<List<LotteryActivity>> getAllActivities() {
        List<LotteryActivity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    /**
     * 根据活动 ID 查询活动详情和奖品信息
     * @param activityId 活动 ID
     * @return 活动详情 DTO
     */
    @GetMapping("/{activityId}/details")
    public ResponseEntity<ActivityDetailDTO> getActivityDetailInfo(@PathVariable Long activityId) {
        LotteryActivity activity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));

        List<LotteryActivityPrize> prizes = prizeService.getPrizesByActivityId(activityId);

        ActivityDetailDTO activityDetailDTO = new ActivityDetailDTO();
        activityDetailDTO.setActivityId(activity.getActivityId());
        activityDetailDTO.setActivityName(activity.getActivityName());
        activityDetailDTO.setDescription(activity.getActivityDesc());
        activityDetailDTO.setStartDate(activity.getStartDate());
        activityDetailDTO.setEndDate(activity.getEndDate());
        activityDetailDTO.setPrizes(prizes);

        return ResponseEntity.ok(activityDetailDTO);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long activityId) {
        activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));
        activityService.deleteActivity(activityId);
        return ResponseEntity.ok("活动已成功删除");
    }
}