package com.example.demo.controller;

import com.example.demo.dao.model.LotteryPrize;
import com.example.demo.service.LotteryPrizeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prize")
public class LotteryPrizeController {

    @Resource
    private LotteryPrizeService prizeService;

    @GetMapping("/all")
    public ResponseEntity<List<LotteryPrize>> getAllPrizes() {
        return ResponseEntity.ok(prizeService.getAllPrizes());
    }

    @GetMapping("/{prizeId}")
    public ResponseEntity<LotteryPrize> getPrizeById(@PathVariable Long prizeId) {
        return prizeService.getPrizeById(prizeId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<LotteryPrize> createPrize(@RequestBody LotteryPrize prize) {
        return ResponseEntity.ok(prizeService.createPrize(prize));
    }

    @PutMapping("/{prizeId}")
    public ResponseEntity<LotteryPrize> updatePrize(@PathVariable Long prizeId, @RequestBody LotteryPrize updatedPrize) {
        return ResponseEntity.ok(prizeService.updatePrize(prizeId, updatedPrize));
    }

    @DeleteMapping("/{prizeId}")
    public ResponseEntity<Void> deletePrize(@PathVariable Long prizeId) {
        prizeService.deletePrize(prizeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<LotteryPrize>> getAvailablePrizes() {
        List<LotteryPrize> availablePrizes = prizeService.getAvailablePrizes();
        return ResponseEntity.ok(availablePrizes);
    }

}