package com.example.demo.lottery.service;

import com.example.demo.lottery.dto.LotteryActivityUserDTO;
import com.example.demo.lottery.dao.model.LotteryActivityUser;
import com.example.demo.lottery.dao.repository.LotteryActivityUserRepository;
import com.example.demo.lottery.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author long_w
 */
@Service
public class LotteryActivityUserService {

    @Autowired
    private LotteryActivityUserRepository activityUserRepository;

    public List<LotteryActivityUserDTO> getUsersByActivityId(Long activityId) {
        return activityUserRepository.findByActivityActivityId(activityId)
                .stream()
                .map(CommonUtil::buildLotteryActivityUserDTO)
                .collect(Collectors.toList());
    }

    public List<LotteryActivityUserDTO> getActivitiesByUserId(Long userId) {
        return activityUserRepository.findByUserUserId(userId)
                .stream()
                .map(CommonUtil::buildLotteryActivityUserDTO)
                .collect(Collectors.toList());
    }

    public LotteryActivityUser saveActivityUser(LotteryActivityUser activityUser) {
        return activityUserRepository.save(activityUser);
    }

    public void updateLotteryAttempts(Long activityUserId, int attempts) {
        LotteryActivityUser activityUser = activityUserRepository.findById(activityUserId).orElseThrow();
        activityUser.setLotteryAttempts(attempts);
        activityUserRepository.save(activityUser);
    }

}
