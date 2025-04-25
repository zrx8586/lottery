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

    // 查询所有活动
    @GetMapping("/all")
    public ResponseEntity<List<LotteryActivity>> getAllActivities() {
        List<LotteryActivity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    // 根据活动ID查询活动
    @GetMapping("/{activityId}")
    public ResponseEntity<LotteryActivity> getActivityById(@PathVariable Long activityId) {
        Optional<LotteryActivity> activity = activityService.getActivityById(activityId);
        return activity.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 创建新活动
    @PostMapping("/create")
    public ResponseEntity<LotteryActivity> createActivity(@RequestBody LotteryActivity activity) {
        LotteryActivity createdActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdActivity);
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
}