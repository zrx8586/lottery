package com.example.demo.service;

import com.example.demo.model.LotteryPrize;
import com.example.demo.repository.LotteryPrizeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 奖品表的 Service
 */
@Service
public class LotteryPrizeService {

    @Resource
    private LotteryPrizeRepository prizeRepository;

    // 查询所有奖品
    public List<LotteryPrize> getAllPrizes() {
        return prizeRepository.findAll();
    }

    // 根据 ID 查询奖品
    public Optional<LotteryPrize> getPrizeById(Long prizeId) {
        return prizeRepository.findById(prizeId);
    }

    // 创建新奖品
    public LotteryPrize createPrize(LotteryPrize prize) {
        return prizeRepository.save(prize);
    }

    // 更新奖品
    public LotteryPrize updatePrize(Long prizeId, LotteryPrize updatedPrize) {
        return prizeRepository.findById(prizeId).map(prize -> {
            prize.setPrizeName(updatedPrize.getPrizeName());
            prize.setPrizeDesc(updatedPrize.getPrizeDesc());
            return prizeRepository.save(prize);
        }).orElseThrow(() -> new RuntimeException("奖品未找到"));
    }

    // 删除奖品
    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }
}