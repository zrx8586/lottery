package com.example.demo.lottery.controller;

import com.example.demo.lottery.dto.response.BaseResponse;
import com.example.demo.lottery.dto.response.LotteryDrawResponse;
import com.example.demo.lottery.dto.response.EligibilityResponse;
import com.example.demo.lottery.dto.response.LotteryRecordResponse;
import com.example.demo.lottery.dto.request.LotteryRecordRequest;
import com.example.demo.lottery.exception.BusinessException;
import com.example.demo.lottery.service.LotteryService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 抽奖控制器
 * @author long_w
 */
@RestController
@RequestMapping("/api/lottery")
public class LotteryController {

    @Resource
    private LotteryService lotteryService;

    @PostMapping("/draw")
    public ResponseEntity<BaseResponse<LotteryDrawResponse>> drawPrize(@RequestParam String username, @RequestParam Long activityId) {
        try {
            LotteryDrawResponse response = lotteryService.drawPrize(username, activityId);
            return ResponseEntity.ok(BaseResponse.success(response));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(BaseResponse.failure(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.failure("系统内部错误，请稍后重试"));
        }
    }

    @GetMapping("/records")
    public ResponseEntity<BaseResponse<List<LotteryRecordResponse>>> getAllLotteryRecords(@RequestParam(required = false) Long activityId) {
        try {
            List<LotteryRecordResponse> records;
            if (activityId != null) {
                records = lotteryService.getLotteryRecordsByActivityId(activityId);
            } else {
                records = lotteryService.getAllLotteryRecords();
            }
            return ResponseEntity.ok(BaseResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.failure("系统内部错误，请稍后重试"));
        }
    }

    @PostMapping("/records/query")
    public ResponseEntity<BaseResponse<List<LotteryRecordResponse>>> getLotteryRecords(@RequestBody LotteryRecordRequest request) {
        try {
            List<LotteryRecordResponse> records = lotteryService.getLotteryRecords(request.getUserId(), request.getActivityId());
            return ResponseEntity.ok(BaseResponse.success(records));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.failure("系统内部错误，请稍后重试"));
        }
    }

    /**
     * 查询用户是否具备抽奖资格及剩余次数
     */
    @GetMapping("/eligibility")
    public ResponseEntity<BaseResponse<EligibilityResponse>> getEligibility(@RequestParam String username,
                                                                            @RequestParam Long activityId) {
        try {
            EligibilityResponse data = lotteryService.getEligibility(username, activityId);
            return ResponseEntity.ok(BaseResponse.success(data));
        } catch (BusinessException e) {
            EligibilityResponse data = new EligibilityResponse();
            data.setEligible(false);
            data.setAttempts(0);
            data.setReason(e.getMessage());
            return ResponseEntity.ok(BaseResponse.success(data));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.failure("系统内部错误，请稍后重试"));
        }
    }
}