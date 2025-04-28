package com.example.demo.lottery.controller;

import com.example.demo.lottery.dto.request.PrizeUpdateRequest;
import com.example.demo.lottery.dao.model.LotteryActivityPrize;
import com.example.demo.lottery.service.LotteryActivityPrizeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 抽奖活动奖品控制器
 * 提供与奖品相关的 API 接口
 */
@RestController
@RequestMapping("/api/activity-prizes")
public class LotteryActivityPrizeController {

    @Resource
    private LotteryActivityPrizeService prizeService;

    /**
     * 查询指定活动的奖品列表
     * @param activityId 活动 ID
     * @return 奖品列表
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<List<LotteryActivityPrize>> getPrizesByActivityId(@PathVariable Long activityId) {
        List<LotteryActivityPrize> prizes = prizeService.getPrizesByActivityId(activityId);
        return ResponseEntity.ok(prizes);
    }

    /**
     * 更新奖品的概率和库存
     * @param activityId 活动 ID
     * @param updates 奖品更新请求
     * @return 更新结果
     */
    @PutMapping("/{activityId}/update")
    public ResponseEntity<String> updatePrizes(
            @PathVariable Long activityId,
            @RequestBody List<PrizeUpdateRequest> updates) {
        prizeService.updatePrizes(activityId, updates);
        return ResponseEntity.ok("奖品信息更新成功");
    }

    /**
     * 查询单个奖品的详细信息
     * @param prizeId 奖品 ID
     * @return 奖品详情
     */
    @GetMapping("/prize/{prizeId}")
    public ResponseEntity<LotteryActivityPrize> getPrizeById(@PathVariable Long prizeId) {
        LotteryActivityPrize prize = prizeService.getPrizeById(prizeId);
        return ResponseEntity.ok(prize);
    }
}