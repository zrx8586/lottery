package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.repository.*;
import com.example.demo.lottery.dto.response.LotteryDrawResponse;
import com.example.demo.lottery.dto.response.EligibilityResponse;
import com.example.demo.lottery.dto.response.LotteryRecordResponse;
import com.example.demo.lottery.exception.BusinessException;
import com.example.demo.lottery.exception.BusinessExceptionEnum;
import com.example.demo.lottery.util.JsonUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 抽奖服务
 * @author long_w
 */
@Service
public class LotteryService {
    private static final Logger logger = LoggerFactory.getLogger(LotteryService.class);
    private static final int MAX_RETRIES = 5;
    private static final int MIN_RETRY_DELAY = 100;
    private static final int MAX_RETRY_DELAY = 200;
    private static final int CACHE_TIMEOUT_MINUTES = 10;
    private static final int LOCK_TIMEOUT_SECONDS = 10;

    @Resource
    private LotteryActivityUserRepository activityUserRepository;

    @Resource
    private LotteryActivityRepository activityRepository;

    @Resource
    private LotteryActivityPrizeRepository activityPrizeRepository;

    @Resource
    private LotteryRecordRepository recordRepository;

    @Resource
    private PrizeProcessingService prizeProcessingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    /**
     * 获取所有中奖记录
     */
    public List<LotteryRecordResponse> getAllLotteryRecords() {
        return recordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID和活动ID获取中奖记录
     */
    public List<LotteryRecordResponse> getLotteryRecords(Long userId, Long activityId) {
        return recordRepository.findByUserIdAndActivityId(userId, activityId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 抽奖
     */
    public LotteryDrawResponse drawPrize(String username, Long activityId) {
        logger.info("开始抽奖: 用户名={}, 活动ID={}", username, activityId);

        // 验证活动是否有效
        LotteryActivity activity = validateActivity(activityId);
        // 验证用户是否存在
        User user = validateUser(username);
        // 验证用户是否有参与活动的资格
        LotteryActivityUser lotteryActivityUser = validateUserDrawCount(activityId, user);

        // 获取奖品列表
        String redisKey = getPrizeCacheKey(activityId);
        List<LotteryActivityPrize> activityPrizes = getCachedPrizesOrInitialize(redisKey, activityId);
        if (CollectionUtils.isEmpty(activityPrizes)) {
            logger.warn("没有可用的奖品: 活动ID={}", activityId);
            throw new BusinessException(BusinessExceptionEnum.PRIZE_NOT_FOUND);
        }

        // 选择并处理奖品
        LotteryActivityPrize selectedPrize = selectAndProcessPrize(redisKey, activityId, activityPrizes, user, activity, lotteryActivityUser);

        // 返回抽奖结果
        if (selectedPrize != null) {
            logger.info("抽奖成功: 用户名={}, 奖品ID={}, 奖品名称={}", 
                username, selectedPrize.getActivityPrizeId(), selectedPrize.getPrize().getPrizeName());
            return createSuccessResponse(selectedPrize);
        } else {
            logger.warn("抽奖失败: 用户名={}, 活动ID={}", username, activityId);
            return createFailureResponse();
        }
    }

    /**
     * 查询抽奖资格与剩余次数
     */
    public EligibilityResponse getEligibility(String username, Long activityId) {
        EligibilityResponse resp = new EligibilityResponse();
        resp.setEligible(false);
        resp.setAttempts(0);
        resp.setReason("");

        // 验证活动
        LotteryActivity activity = validateActivity(activityId);

        // 验证用户
        User user = validateUser(username);

        // 查询或校验次数
        LotteryActivityUser l = activityUserRepository.findByUserIdAndActivityId(user.getUserId(), activityId);
        if (l == null) {
            resp.setEligible(false);
            resp.setAttempts(0);
            resp.setReason("用户无该活动资格");
            return resp;
        }

        int attempts = l.getLotteryAttempts();
        resp.setAttempts(attempts);
        if (attempts > 0) {
            resp.setEligible(true);
        } else {
            resp.setEligible(false);
            resp.setReason("剩余次数为0");
        }
        return resp;
    }

    /**
     * 验证用户抽奖次数
     */
    private LotteryActivityUser validateUserDrawCount(Long activityId, User user) {
        LotteryActivityUser lotteryActivityUser = activityUserRepository.findByUserIdAndActivityId(user.getUserId(), activityId);
        if (lotteryActivityUser == null) {
            throw new BusinessException(BusinessExceptionEnum.USER_NO_ACTIVITY_QUALIFICATION);
        }
        if (lotteryActivityUser.getLotteryAttempts() <= 0) {
            throw new BusinessException(BusinessExceptionEnum.USER_NO_ATTEMPTS);
        }
        return lotteryActivityUser;
    }

    /**
     * 验证用户是否存在
     */
    private User validateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException(BusinessExceptionEnum.USER_NOT_FOUND));
    }

    /**
     * 验证活动是否有效
     */
    private LotteryActivity validateActivity(Long activityId) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new BusinessException(BusinessExceptionEnum.ACTIVITY_NOT_FOUND));
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartDate())) {
            throw new BusinessException(BusinessExceptionEnum.ACTIVITY_NOT_STARTED);
        }
        if (now.isAfter(activity.getEndDate())) {
            throw new BusinessException(BusinessExceptionEnum.ACTIVITY_ENDED);
        }
        return activity;
    }

    /**
     * 选择奖品并处理
     */
    private LotteryActivityPrize selectAndProcessPrize(String redisKey, Long activityId,
                                                     List<LotteryActivityPrize> activityPrizes,
                                                     User user, LotteryActivity activity,
                                                     LotteryActivityUser lotteryActivityUser) {
        logger.info("开始选择奖品: 活动ID={}, 用户ID={}", activityId, user.getUserId());

        try {
            int retries = 0;
            while (retries < MAX_RETRIES) {
                // 根据概率选择奖品
                LotteryActivityPrize selectedPrize = selectPrizeBasedOnProbability(activityPrizes);

                // 如果选中的奖品为null，直接重试
                if (selectedPrize == null || selectedPrize.getQuantity() <= 0) {
                    logger.warn("用户={}, 选中的奖品无效，继续尝试: 活动ID={}, 奖品ID={}", 
                        user.getUsername(), activityId, selectedPrize.getActivityPrizeId());
                    retries++;
                    continue;
                }

                // 尝试获取锁并处理奖品
                boolean isProcessed = tryLockAndProcessPrize(redisKey, activityId, selectedPrize, user, activity, lotteryActivityUser);
                if (isProcessed) {
                    logger.info("奖品处理成功: 奖品ID={}, 用户ID={}", selectedPrize.getActivityPrizeId(), user.getUserId());
                    return selectedPrize;
                }

                logger.warn("用户={}, 奖品处理失败，继续尝试其他奖品: 活动ID={}, 奖品ID={}",
                    user.getUsername(), activityId, selectedPrize.getActivityPrizeId());

                // 增加重试次数并随机延迟
                retries++;
                try {
                    Thread.sleep(MIN_RETRY_DELAY + random.nextInt(MAX_RETRY_DELAY - MIN_RETRY_DELAY));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(BusinessExceptionEnum.SYSTEM_ERROR, e);
                }
            }

            logger.warn("用户={}, 超过最大重试次数，抽奖失败: 活动ID={}", user.getUsername(), activityId);
            return null;
        } catch (BusinessException e) {
            logger.error("选择奖品业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("选择奖品系统异常: {}", e.getMessage(), e);
            throw new BusinessException(BusinessExceptionEnum.SYSTEM_ERROR, e);
        }
    }

    /**
     * 获取缓存的奖品列表或初始化缓存
     */
    private List<LotteryActivityPrize> getCachedPrizesOrInitialize(String redisKey, Long activityId) {
        logger.info("获取或初始化奖品缓存: 活动ID={}", activityId);
        try {
            String cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isEmpty(cachedPrizesJson)) {
                logger.info("缓存为空，开始初始化: 活动ID={}", activityId);
                initializePrizeCache(redisKey, activityId);
                cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
            }
            return JsonUtil.fromJsonToList(cachedPrizesJson, LotteryActivityPrize.class);
        } catch (Exception e) {
            logger.error("获取奖品缓存失败: 活动ID={}, 错误信息={}", activityId, e.getMessage(), e);
            throw new BusinessException(BusinessExceptionEnum.SYSTEM_ERROR, e);
        }
    }

    /**
     * 初始化Redis缓存
     */
    private void initializePrizeCache(String redisKey, Long activityId) {
        logger.info("初始化奖品缓存: 活动ID={}", activityId);
        String initLockKey = getLockKey("activity", activityId, "init");
        boolean lockAcquired = false;
        try {
            lockAcquired = redisTemplate.opsForValue().setIfAbsent(initLockKey, "1", LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (lockAcquired) {
                List<LotteryActivityPrize> prizes = activityPrizeRepository.findByActivityId(activityId);
                if (CollectionUtils.isEmpty(prizes)) {
                    throw new BusinessException(BusinessExceptionEnum.PRIZE_NOT_FOUND);
                }
                redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(prizes), CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
            } else {
                while (redisTemplate.hasKey(initLockKey)) {
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(BusinessExceptionEnum.SYSTEM_ERROR, e);
        } finally {
            if (lockAcquired) {
                redisTemplate.delete(initLockKey);
            }
        }
    }

    /**
     * 尝试获取锁并处理奖品
     */
    private boolean tryLockAndProcessPrize(String redisKey, Long activityId, LotteryActivityPrize prize,
                                         User user, LotteryActivity activity, LotteryActivityUser lotteryActivityUser) {
        logger.info("尝试获取锁并处理奖品: 活动ID={}, 奖品ID={}", activityId, prize.getActivityPrizeId());
        String lockKey = getLockKey("activity", activityId, "prize:" + prize.getPrize().getPrizeId());
        String lockValue = String.valueOf(System.currentTimeMillis());
        boolean lockAcquired = false;
        try {
            lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (lockAcquired) {
                prizeProcessingService.processPrize(redisKey, prize, user, activity, lotteryActivityUser);
                return true;
            }
        } finally {
            if (lockAcquired && lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
        return false;
    }

    /**
     * 创建成功响应
     */
    private LotteryDrawResponse createSuccessResponse(LotteryActivityPrize prize) {
        LotteryDrawResponse response = new LotteryDrawResponse();
        response.setSuccess(true);
        response.setMessage("恭喜中奖！");
        response.setPrizeName(prize.getPrize().getPrizeName());
        return response;
    }

    /**
     * 创建失败响应
     */
    private LotteryDrawResponse createFailureResponse() {
        LotteryDrawResponse response = new LotteryDrawResponse();
        response.setSuccess(false);
        response.setMessage("所有奖品已发完");
        return response;
    }

    /**
     * 根据概率选择奖品
     */
    private LotteryActivityPrize selectPrizeBasedOnProbability(List<LotteryActivityPrize> activityPrizes) {
        double[] cumulativeProbabilities = new double[activityPrizes.size()];
        cumulativeProbabilities[0] = activityPrizes.get(0).getProbability();
        for (int i = 1; i < activityPrizes.size(); i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + activityPrizes.get(i).getProbability();
        }

        double randomValue = random.nextDouble() * cumulativeProbabilities[cumulativeProbabilities.length - 1];
        int index = binarySearch(cumulativeProbabilities, randomValue);
        return activityPrizes.get(index);
    }

    /**
     * 二分查找快速定位随机数对应的奖品
     */
    private int binarySearch(double[] cumulativeProbabilities, double value) {
        int low = 0, high = cumulativeProbabilities.length - 1;
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (cumulativeProbabilities[mid] < value) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }

    /**
     * 根据活动ID获取中奖记录
     */
    public List<LotteryRecordResponse> getLotteryRecordsByActivityId(Long activityId) {
        return recordRepository.findByActivityId(activityId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将 LotteryRecord 转换为 LotteryRecordDTO
     */
    private LotteryRecordResponse convertToDTO(LotteryRecord record) {
        LotteryRecordResponse dto = new LotteryRecordResponse();
        dto.setUsername(record.getUser().getUsername());
        dto.setActivityName(record.getLotteryActivity().getActivityName());
        dto.setPrizeName(record.getLotteryPrize().getPrizeName());
        dto.setWonAt(record.getWonAt());
        return dto;
    }

    /**
     * 获取奖品缓存key
     */
    private String getPrizeCacheKey(Long activityId) {
        return "activity:" + activityId + ":prizes";
    }

    /**
     * 获取锁的key
     */
    private String getLockKey(String type, Long id, String suffix) {
        return String.format("lock:%s:%d:%s", type, id, suffix);
    }
}
