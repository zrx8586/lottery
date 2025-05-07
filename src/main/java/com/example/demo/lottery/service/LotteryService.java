package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.*;
import com.example.demo.lottery.dao.repository.*;
import com.example.demo.lottery.dto.response.LotteryDrawResponse;
import com.example.demo.lottery.dto.response.LotteryRecordResponse;
import com.example.demo.lottery.exception.BusinessException;
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
 * @author long_w
 */
@Service
public class LotteryService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryService.class);

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

    private Random random = new Random();

    // 获取所有中奖记录
    public List<LotteryRecordResponse> getAllLotteryRecords() {
        return recordRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 根据用户ID获取中奖记录
    public List<LotteryRecordResponse> getLotteryRecords(Long userId, Long activityId) {
        return recordRepository.findByUserIdAndActivityId(userId, activityId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LotteryDrawResponse drawPrize(String username, Long activityId) {
        logger.info("开始抽奖: 用户名={}, 活动ID={}", username, activityId);

        // 验证活动是否有效
        LotteryActivity activity = validateActivity(activityId);
        // 验证用户是否存在
        User user = validateUser(username);
        // 验证用户是否有参与活动的资格
        LotteryActivityUser lotteryActivityUser = validateUserDrawCount(activityId, user);

        // 获取奖品列表
        String redisKey = "activity:" + activityId + ":prizes";
        List<LotteryActivityPrize> activityPrizes = getCachedPrizesOrInitialize(redisKey, activityId);
        if (CollectionUtils.isEmpty(activityPrizes)) {
            logger.warn("没有可用的奖品: 活动ID={}", activityId);
            throw new BusinessException(1003, "没有可用的奖品");
        }

        // 选择并处理奖品
        LotteryActivityPrize selectedPrize = selectAndProcessPrize(redisKey, activityId, activityPrizes, user, activity, lotteryActivityUser);

        // 返回抽奖结果
        if (selectedPrize != null) {
            logger.info("抽奖成功: 用户名={}, 奖品ID={}, 奖品名称={}", username, selectedPrize.getActivityPrizeId(), selectedPrize.getPrize().getPrizeName());
            return createSuccessResponse(selectedPrize);
        } else {
            logger.warn("抽奖失败: 用户名={}, 活动ID={}", username, activityId);
            return createFailureResponse();
        }
    }

    private LotteryActivityUser validateUserDrawCount(Long activityId, User user) {
        LotteryActivityUser lotteryActivityUser = activityUserRepository.findByUserIdAndActivityId(user.getUserId(), activityId);
        if (lotteryActivityUser == null) {
            throw new BusinessException(1003, "用户没有参与活动的资格");
        }
        if (lotteryActivityUser.getLotteryAttempts() <= 0) {
            throw new BusinessException(1004, "用户抽奖次数已用完");
        }
        return lotteryActivityUser;
    }

    private User validateUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    private LotteryActivity validateActivity(Long activityId) {
        LotteryActivity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("活动不存在"));
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartDate())) {
            throw new BusinessException(1001, "活动尚未开始");
        }
        if (now.isAfter(activity.getEndDate())) {
            throw new BusinessException(1002, "活动已结束");
        }
        return activity;
    }

    /**
     * 选择奖品并处理
     * @param redisKey Redis缓存的key
     * @param activityId 活动ID
     * @param activityPrizes 奖品列表
     * @param user 中奖用户
     * @param activity 活动
     * @return LotteryActivityPrize
     */
    private LotteryActivityPrize selectAndProcessPrize(String redisKey, Long activityId,
                                                       List<LotteryActivityPrize> activityPrizes,
                                                       User user, LotteryActivity activity,
                                                       LotteryActivityUser lotteryActivityUser) {
        logger.info("开始选择奖品: 活动ID={}, 用户ID={}", activityId, user.getUserId());

        try {
            int maxRetries = 5; // 最大重试次数
            int retries = 0;

            while (retries < maxRetries) {
                // 根据概率选择奖品
                LotteryActivityPrize selectedPrize = selectPrizeBasedOnProbability(activityPrizes);

                // 如果选中的奖品为null，直接重试
                if (selectedPrize == null || selectedPrize.getQuantity() <= 0) {
                    logger.warn("用户={}, 选中的奖品无效，继续尝试: 活动ID={}, 奖品ID={}", user.getUsername(), activityId, selectedPrize.getActivityPrizeId());
                    retries++;
                    continue;
                }

                // 尝试获取锁并处理奖品
                boolean isProcessed = tryLockAndProcessPrize(redisKey, activityId, selectedPrize, user, activity, lotteryActivityUser);
                if (isProcessed) {
                    // 如果成功处理奖品，返回选中的奖品
                    logger.info("奖品处理成功: 奖品ID={}, 用户ID={}", selectedPrize.getActivityPrizeId(), user.getUserId());
                    return selectedPrize;
                } else {
                    // 如果处理失败，可能是因为库存不足，重新选择奖品
                    logger.warn("用户={}, 奖品处理失败，继续尝试其他奖品: 活动ID={}, 奖品ID={}",
                        user.getUsername(), activityId, selectedPrize.getActivityPrizeId());
                }

                // 增加重试次数并随机延迟
                retries++;
                try {
                    Thread.sleep(100 + random.nextInt(100)); // 随机延迟 100-200ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("线程被中断", e);
                }
            }

            // 如果超过最大重试次数，返回 null
            logger.warn("用户={}, 超过最大重试次数，抽奖失败: 活动ID={}", user.getUsername(), activityId);
            return null;
        } catch (Exception e) {
            logger.error("选择奖品失败: 活动ID={}, 用户ID={}, 错误信息={}", activityId, user.getUserId(), e.getMessage(), e);
            throw new BusinessException(500, "选择奖品失败");
        }
    }

    /**
     * 获取缓存的奖品列表或初始化缓存
     * @param redisKey Redis缓存的key
     * @param activityId 活动ID
     * @return List<LotteryActivityPrize>
     */
    private List<LotteryActivityPrize> getCachedPrizesOrInitialize(String redisKey, Long activityId) {
        logger.info("获取或初始化奖品缓存: 活动ID={}", activityId);
        // 从 Redis 中获取缓存的 JSON 字符串
        try {
            String cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isEmpty(cachedPrizesJson)) {
                // 如果缓存为空，初始化缓存
                logger.info("缓存为空，开始初始化: 活动ID={}", activityId);
                initializePrizeCache(redisKey, activityId);
                cachedPrizesJson = redisTemplate.opsForValue().get(redisKey);
            }
            // 将 JSON 字符串反序列化为奖品列表
            return JsonUtil.fromJsonToList(cachedPrizesJson, LotteryActivityPrize.class);
        }catch (Exception e) {
            logger.error("获取奖品缓存失败: 活动ID={}, 错误信息={}", activityId, e.getMessage(), e);
            throw new BusinessException(500, "获取奖品缓存失败");
        }
    }

    /**
     * 初始化Redis缓存
     * @param redisKey Redis缓存的key
     * @param activityId 活动ID
     */
    private void initializePrizeCache(String redisKey, Long activityId) {
        logger.info("初始化奖品缓存: 活动ID={}", activityId);
        String initLockKey = "lock:activity:" + activityId + ":init";
        boolean lockAcquired = false;
        try {
            lockAcquired = redisTemplate.opsForValue().setIfAbsent(initLockKey, "1", 10, TimeUnit.SECONDS);
            if (lockAcquired) {
                // 从数据库中获取所有奖品
                List<LotteryActivityPrize> prizes = activityPrizeRepository.findByActivityId(activityId);

                if (CollectionUtils.isEmpty(prizes)) {
                    throw new BusinessException(1003, "没有可用的奖品");
                }

                // 将奖品列表作为一个整体缓存到 Redis
                redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(prizes), 10, TimeUnit.MINUTES);
            } else {
                // 等待其他线程完成缓存初始化
                while (redisTemplate.hasKey(initLockKey)) {
                    Thread.sleep(100);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("线程被中断", e);
        } finally {
            if (lockAcquired) {
                redisTemplate.delete(initLockKey);
            }
        }
    }

    /**
     * 尝试获取锁并处理奖品
     *
     * @param redisKey            Redis缓存的key
     * @param activityId          活动ID
     * @param prize               奖品
     * @param user                中奖用户
     * @param activity            活动
     * @param lotteryActivityUser 抽奖活动用户
     * @return boolean
     */
    private boolean tryLockAndProcessPrize(String redisKey, Long activityId, LotteryActivityPrize prize,
                                           User user, LotteryActivity activity, LotteryActivityUser lotteryActivityUser) {
        logger.info("尝试获取锁并处理奖品: 活动ID={}, 奖品ID={}", activityId, prize.getActivityPrizeId());
        String lockKey = "lock:activity:" + activityId + ":prize:" + prize.getPrize().getPrizeId();
        String lockValue = String.valueOf(System.currentTimeMillis()); // 唯一标识
        boolean lockAcquired = false;
        try {
            lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
            if (lockAcquired) {
                prizeProcessingService.processPrize(redisKey, prize, user, activity, lotteryActivityUser);
                return true;
            }
        } finally {
            // 确保释放锁时验证唯一标识
            if (lockAcquired && lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
        return false;
    }

    private LotteryDrawResponse createSuccessResponse(LotteryActivityPrize prize) {
        LotteryDrawResponse response = new LotteryDrawResponse();
        response.setSuccess(true);
        response.setMessage("恭喜中奖！");
        response.setPrizeName(prize.getPrize().getPrizeName());
        return response;
    }

    private LotteryDrawResponse createFailureResponse() {
        LotteryDrawResponse response = new LotteryDrawResponse();
        response.setSuccess(false);
        response.setMessage("所有奖品已发完");
        return response;
    }

    private LotteryActivityPrize selectPrizeBasedOnProbability(List<LotteryActivityPrize> activityPrizes) {
        // 预计算累计概率
        double[] cumulativeProbabilities = new double[activityPrizes.size()];
        cumulativeProbabilities[0] = activityPrizes.get(0).getProbability();
        for (int i = 1; i < activityPrizes.size(); i++) {
            cumulativeProbabilities[i] = cumulativeProbabilities[i - 1] + activityPrizes.get(i).getProbability();
        }

        // 生成随机数
        double randomValue = random.nextDouble() * cumulativeProbabilities[cumulativeProbabilities.length - 1];

        // 使用二分查找定位奖品
        int index = binarySearch(cumulativeProbabilities, randomValue);
        return activityPrizes.get(index);
    }

    /**
     * 二分查找快速定位随机数对应的奖品
     * @param cumulativeProbabilities
     * @param value
     * @return
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

    public List<LotteryRecordResponse> getLotteryRecordsByActivityId(Long activityId) {
        return recordRepository.findByActivityId(activityId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    // 将 LotteryRecord 转换为 LotteryRecordDTO
    private LotteryRecordResponse convertToDTO(LotteryRecord record) {
        LotteryRecordResponse dto = new LotteryRecordResponse();
        dto.setUsername(record.getUser().getUsername());
        dto.setActivityName(record.getLotteryActivity().getActivityName());
        dto.setPrizeName(record.getLotteryPrize().getPrizeName());
        dto.setWonAt(record.getWonAt());
        return dto;
    }

}
