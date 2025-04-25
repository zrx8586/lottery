package com.example.demo.service;

import com.example.demo.dto.LotteryActivityUserDTO;
import com.example.demo.model.LotteryActivityUser;
import com.example.demo.repository.LotteryActivityUserRepository;
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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LotteryActivityUserDTO> getActivitiesByUserId(Long userId) {
        return activityUserRepository.findByUserUserId(userId)
                .stream()
                .map(this::convertToDTO)
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

    private LotteryActivityUserDTO convertToDTO(LotteryActivityUser activityUser) {
        LotteryActivityUserDTO dto = new LotteryActivityUserDTO();
        dto.setActivityUserId(activityUser.getActivityUserId());
        dto.setActivityId(activityUser.getActivity().getActivityId());
        dto.setActivityName(activityUser.getActivity().getActivityName());
        dto.setUserId(activityUser.getUser().getUserId());
        dto.setUsername(activityUser.getUser().getUsername());
        dto.setLotteryAttempts(activityUser.getLotteryAttempts());
        return dto;
    }
}
