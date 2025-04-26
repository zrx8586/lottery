package com.example.demo.service;

import com.example.demo.dto.request.PrizeUpdateRequest;
import com.example.demo.model.LotteryActivityPrize;
import com.example.demo.repository.LotteryActivityPrizeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 抽奖活动奖品服务类
 * 处理与奖品相关的业务逻辑
 * @author long_w
 */
@Service
public class LotteryActivityPrizeService {

    @Resource
    private LotteryActivityPrizeRepository activityPrizeRepository;

    /**
     * 根据活动 ID 查询奖品列表
     * @param activityId 活动 ID
     * @return 奖品列表
     */
    public List<LotteryActivityPrize> getPrizesByActivityId(Long activityId) {
        return activityPrizeRepository.findByActivityId(activityId);
    }

    /**
     * 更新奖品的概率和库存
     * @param activityId 活动 ID
     * @param updates 奖品更新请求列表
     */
    @Transactional
    public void updatePrizes(Long activityId, List<PrizeUpdateRequest> updates) {
        List<LotteryActivityPrize> prizes = activityPrizeRepository.findByActivityId(activityId);

        for (PrizeUpdateRequest update : updates) {
            Optional<LotteryActivityPrize> prizeOptional = prizes.stream()
                    .filter(prize -> prize.getPrize().getPrizeId().equals(update.getPrizeId()))
                    .findFirst();

            if (prizeOptional.isPresent()) {
                LotteryActivityPrize prize = prizeOptional.get();
                prize.setProbability(update.getProbability());
                prize.setQuantity(update.getQuantity());
                activityPrizeRepository.save(prize);
            }
        }
    }

    /**
     * 根据奖品 ID 查询奖品详情
     * @param prizeId 奖品 ID
     * @return 奖品详情
     */
    public LotteryActivityPrize getPrizeById(Long prizeId) {
        return activityPrizeRepository.findById(prizeId)
                .orElseThrow(() -> new IllegalArgumentException("奖品不存在"));
    }


    /**
     * 根据活动 ID 删除所有奖品关系
     * @param activityId 活动 ID
     */
    @Transactional
    public void deletePrizesByActivityId(Long activityId) {
        activityPrizeRepository.deleteByActivityId(activityId);
    }

}