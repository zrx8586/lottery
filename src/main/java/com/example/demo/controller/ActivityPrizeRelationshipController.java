package com.example.demo.controller;

import com.example.demo.dto.ActivityDetailDTO;
import com.example.demo.dto.ActivityPrizeDTO;
import com.example.demo.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.model.LotteryActivity;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.service.ActivityPrizeRelationshipService;
import com.example.demo.service.LotteryActivityPrizeService;
import com.example.demo.service.LotteryActivityService;
import com.example.demo.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-prize-relationship")
public class ActivityPrizeRelationshipController {

    @Autowired
    private LotteryActivityService activityService;

    @Autowired
    private ActivityPrizeRelationshipService activityPrizeRelationshipService;

    @Autowired
    private LotteryActivityPrizeService activityPrizeService;

    // 查询所有活动奖品关系
    @GetMapping("/all")
    public ResponseEntity<List<ActivityPrizeRelationshipDTO>> getAllActivityPrizeRelationships() {
        List<ActivityPrizeRelationshipDTO> allActivityPrizeRelationships = activityPrizeRelationshipService.getAll();
        return ResponseEntity.ok(allActivityPrizeRelationships);
    }

    // 根据活动 ID 查询活动详情（包含奖品信息）
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityPrizeRelationshipDTO> getActivityById(@PathVariable Long activityId) {
        ActivityPrizeRelationshipDTO activityWithPrizes = activityPrizeRelationshipService.getActivityWithPrizes(activityId);
        return ResponseEntity.ok(activityWithPrizes);
    }

    @PutMapping("/{activityId}")
    public ResponseEntity<String> updateActivityPrizeRelationship(
            @PathVariable Long activityId,
            @RequestBody ActivityPrizeRelationshipDTO relationshipDTO) {
        try {
            // 确保活动 ID 一致
            if (!activityId.equals(relationshipDTO.getActivityId())) {
                return ResponseEntity.badRequest().body("活动 ID 不匹配");
            }

            // 清除旧的奖品关系
            activityPrizeService.deletePrizesByActivityId(activityId);

            // 创建新的奖品关系
            activityPrizeRelationshipService.createActivityPrizeRelationship(relationshipDTO);

            return ResponseEntity.ok("活动与奖品绑定关系更新成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("更新绑定关系失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新绑定关系失败: " + e.getMessage());
        }
    }

    @GetMapping("/{activityId}/details")
    public ResponseEntity<ActivityDetailDTO> getActivityDetailInfo(@PathVariable Long activityId) {
        LotteryActivity activity = activityService.getActivityById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在，ID: " + activityId));

        List<LotteryActivityPrize> prizes = activityPrizeService.getPrizesByActivityId(activityId);

        List<ActivityPrizeDTO> prizeDTOs = CommonUtil.getActivityPrizeDTOS(prizes);
        ;

        ActivityDetailDTO activityDetailDTO = CommonUtil.buildActivityDetailDTO(activity, prizeDTOs);

        return ResponseEntity.ok(activityDetailDTO);
    }


    @PostMapping("/create")
    public ResponseEntity<String> createActivityPrizeRelationship(@RequestBody ActivityPrizeRelationshipDTO relationshipDTO) {
        try {
            // 调用服务层方法保存绑定关系
            activityPrizeRelationshipService.createActivityPrizeRelationship(relationshipDTO);
            return ResponseEntity.ok("活动与奖品绑定关系创建成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("创建绑定关系失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建绑定关系失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/relationship/{activityPrizeId}")
    public ResponseEntity<String> deleteActivityPrizeRelationship(@PathVariable Long activityPrizeId) {
        try {
            activityPrizeService.deletePrizeById(activityPrizeId);
            return ResponseEntity.ok("活动奖品关系删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("删除活动奖品关系失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除活动奖品关系失败: " + e.getMessage());
        }
    }
}
