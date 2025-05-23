package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.LotteryPrize;
import com.example.demo.lottery.dao.repository.LotteryPrizeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LotteryPrizeService {

    @Resource
    private LotteryPrizeRepository prizeRepository;

    public List<LotteryPrize> getAllPrizes() {
        return prizeRepository.findAll();
    }

    public Optional<LotteryPrize> getPrizeById(Long prizeId) {
        return prizeRepository.findById(prizeId);
    }

    public LotteryPrize createPrize(LotteryPrize prize) {
        return prizeRepository.save(prize);
    }

    public LotteryPrize updatePrize(Long prizeId, LotteryPrize updatedPrize) {
        return prizeRepository.findById(prizeId).map(prize -> {
            prize.setPrizeName(updatedPrize.getPrizeName());
            prize.setPrizeDesc(updatedPrize.getPrizeDesc());
            prize.setPrizeImageUrl(updatedPrize.getPrizeImageUrl());
            prize.setPrizeCategory(updatedPrize.getPrizeCategory());
            prize.setPrizeValue(updatedPrize.getPrizeValue());
            prize.setStockQuantity(updatedPrize.getStockQuantity());
            prize.setIsActive(updatedPrize.getIsActive());
            return prizeRepository.save(prize);
        }).orElseThrow(() -> new RuntimeException("Prize not found"));
    }

    public void deletePrize(Long prizeId) {
        prizeRepository.deleteById(prizeId);
    }

    public List<LotteryPrize> getAvailablePrizes() {
        return prizeRepository.findByIsActiveTrue();
    }
}