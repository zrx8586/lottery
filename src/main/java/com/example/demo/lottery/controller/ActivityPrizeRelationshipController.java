package com.example.demo.lottery.controller;

import com.example.demo.lottery.dto.ActivityDetailDTO;
import com.example.demo.lottery.dto.ActivityPrizeDTO;
import com.example.demo.lottery.dto.ActivityPrizeRelationshipDTO;
import com.example.demo.lottery.dao.model.LotteryActivity;
import com.example.demo.lottery.dao.model.LotteryActivityPrize;
import com.example.demo.lottery.service.ActivityPrizeRelationshipService;
import com.example.demo.lottery.service.LotteryActivityPrizeService;
import com.example.demo.lottery.service.LotteryActivityService;
import com.example.demo.lottery.util.CommonUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activity-prize-relationship")
public class ActivityPrizeRelationshipController {

    @Resource
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

    @GetMapping("/{activityId}/details")
    public ResponseEntity<ActivityDetailDTO> getActivityDetailInfo(@PathVariable Long activityId) {
        Optional<LotteryActivity> activity = activityService.getActivityById(activityId);
        if (activity.isEmpty()) {
            throw new IllegalArgumentException("活动不存在，ID: " + activityId);
        }

        List<LotteryActivityPrize> prizes = activityPrizeService.getPrizesByActivityId(activityId);
        if (prizes == null || prizes.isEmpty()) {
            throw new IllegalArgumentException("活动没有配置奖品，ID: " + activityId);
        }

        List<ActivityPrizeDTO> prizeDTOs = CommonUtil.getActivityPrizeDTOS(prizes);
        ActivityDetailDTO activityDetailDTO = CommonUtil.buildActivityDetailDTO(activity.get(), prizeDTOs);

        return ResponseEntity.ok(activityDetailDTO);
    }

    // 根据活动 ID 查询活动详情（包含奖品信息）
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityPrizeRelationshipDTO> getActivityById(@PathVariable Long activityId) {
        ActivityPrizeRelationshipDTO activityWithPrizes = activityPrizeRelationshipService.getActivityWithPrizes(activityId);
        return ResponseEntity.ok(activityWithPrizes);
    }

    // 创建活动与奖品绑定关系
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

    // 更新活动与奖品绑定关系
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

    /**
     * 删除活动奖品关系
     * @param activityId 活动 ID
     * @return 删除结果
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<String> deleteActivityPrizeRelationshipsByActivityId(@PathVariable Long activityId) {
        try {
            // 根据活动 ID 删除所有奖品关系
            activityPrizeService.deletePrizesByActivityId(activityId);
            return ResponseEntity.ok("活动奖品关系删除成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("删除活动奖品关系失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除活动奖品关系失败: " + e.getMessage());
        }
    }

    // 获取活动的所有奖品
    @GetMapping("/{activityId}/prizes")
    public ResponseEntity<List<LotteryActivityPrize>> getActivityPrizes(@PathVariable Long activityId) {
        List<LotteryActivityPrize> prizes = activityService.getActivityPrizes(activityId);
        return ResponseEntity.ok(prizes);
    }

    // 添加奖品到活动
    @PostMapping("/{activityId}/prizes")
    public ResponseEntity<LotteryActivityPrize> addPrizeToActivity(
            @PathVariable Long activityId,
            @RequestBody LotteryActivityPrize activityPrize) {
        LotteryActivityPrize savedPrize = activityService.addPrizeToActivity(activityId, activityPrize);
        return ResponseEntity.ok(savedPrize);
    }

    // 更新活动奖品信息
    @PutMapping("/{activityId}/prizes/{prizeId}")
    public ResponseEntity<LotteryActivityPrize> updateActivityPrize(
            @PathVariable Long activityId,
            @PathVariable Long prizeId,
            @RequestBody LotteryActivityPrize activityPrize) {
        LotteryActivityPrize updatedPrize = activityService.updateActivityPrize(activityId, prizeId, activityPrize);
        return ResponseEntity.ok(updatedPrize);
    }

    // 从活动中移除奖品
    @DeleteMapping("/{activityId}/prizes/{prizeId}")
    public ResponseEntity<String> removePrizeFromActivity(
            @PathVariable Long activityId,
            @PathVariable Long prizeId) {
        activityService.removePrizeFromActivity(activityId, prizeId);
        return ResponseEntity.ok("奖品已成功从活动中移除");
    }
}
