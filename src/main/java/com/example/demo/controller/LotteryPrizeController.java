package com.example.demo.controller;

import com.example.demo.model.LotteryPrize;
import com.example.demo.service.LotteryPrizeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 奖品表的 Controller
 */
@RestController
@RequestMapping("/api/prize")
public class LotteryPrizeController {

    @Resource
    private LotteryPrizeService prizeService;

    // 查询所有奖品
    @GetMapping("/all")
    public ResponseEntity<List<LotteryPrize>> getAllPrizes() {
        List<LotteryPrize> prizes = prizeService.getAllPrizes();
        return ResponseEntity.ok(prizes);
    }

    // 根据 ID 查询奖品
    @GetMapping("/{prizeId}")
    public ResponseEntity<LotteryPrize> getPrizeById(@PathVariable Long prizeId) {
        return prizeService.getPrizeById(prizeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 创建新奖品
    @PostMapping("/create")
    public ResponseEntity<LotteryPrize> createPrize(@RequestBody LotteryPrize prize) {
        LotteryPrize createdPrize = prizeService.createPrize(prize);
        return ResponseEntity.ok(createdPrize);
    }

    // 更新奖品
    @PutMapping("/{prizeId}")
    public ResponseEntity<LotteryPrize> updatePrize(@PathVariable Long prizeId, @RequestBody LotteryPrize updatedPrize) {
        LotteryPrize prize = prizeService.updatePrize(prizeId, updatedPrize);
        return ResponseEntity.ok(prize);
    }

    // 删除奖品
    @DeleteMapping("/{prizeId}")
    public ResponseEntity<Void> deletePrize(@PathVariable Long prizeId) {
        prizeService.deletePrize(prizeId);
        return ResponseEntity.noContent().build();
    }
}