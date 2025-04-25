package com.example.demo.controller;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.service.ActivityPrizeRelationshipService;
import com.example.demo.service.LotteryActivityPrizeService;
import com.example.demo.service.LotteryActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity-prize-relationship")
public class ActivityPrizeRelationshipController {

    @Autowired
    private LotteryActivityService activityService;

    @Autowired
    private ActivityPrizeRelationshipService activityPrizeRelationshipService;

    @Autowired
    private LotteryActivityPrizeService prizeService;

    // 查询所有活动
    @GetMapping("/all")
    public ResponseEntity<List<LotteryActivity>> getAllActivities() {
        List<LotteryActivity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    // 根据活动 ID 查询活动详情（包含奖品信息）
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityPrizeRelationshipDTO> getActivityById(@PathVariable Long activityId) {
        ActivityPrizeRelationshipDTO activityWithPrizes = activityPrizeRelationshipService.getActivityWithPrizes(activityId);
        return ResponseEntity.ok(activityWithPrizes);
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
        activityDetailDTO.setActivityDesc(activity.getActivityDesc());
        activityDetailDTO.setStartDate(activity.getStartDate());
        activityDetailDTO.setEndDate(activity.getEndDate());
        activityDetailDTO.setPrizes(prizes);

        return ResponseEntity.ok(activityDetailDTO);
    }

}
