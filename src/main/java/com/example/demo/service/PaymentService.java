package com.example.demo.service;

import com.example.demo.common.PaymentStatusEnum;
import com.example.demo.dto.request.ProcessPaymentRequest;
import com.example.demo.dao.model.IdempotentRecord;
import com.example.demo.dao.repository.IdempotentRecordRepository;
import com.example.demo.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 支付服务类
 * @author long_w
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Resource
    private IdempotentRecordRepository idempotentRecordRepository;

    @Resource
    private RedisUtil redisUtil;

    private static final String LOCK_PREFIX = "payment_lock:";
    private static final int LOCK_TIMEOUT = 10; // 锁的超时时间（秒）

    /**
     * 处理支付请求
     *
     * @param paymentRequest 支付请求
     * @return 支付结果
     */
    public String processPayment(ProcessPaymentRequest paymentRequest) {
        String idempotencyKey = paymentRequest.getIdempotencyKey();
        double amount = paymentRequest.getAmount();
        Long orderId = paymentRequest.getOrderId();
        String lockKey = LOCK_PREFIX + idempotencyKey;
        String lockValue = UUID.randomUUID().toString(); // 生成唯一标识

        // 尝试获取分布式锁
        if (!redisUtil.setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT)) {
            logger.warn("幂等键 {} 正在处理中，拒绝重复请求", idempotencyKey);
            return "Request is already being processed.";
        }

        // 创建一个定时任务线程池，用于锁续期
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        try {
            // 启动锁续期任务，每 5 秒延长一次锁的过期时间
            scheduler.scheduleAtFixedRate(() -> {
                boolean renewed = redisUtil.renewLock(lockKey, lockValue, LOCK_TIMEOUT);
                if (renewed) {
                    logger.info("锁续期成功: {}", lockKey);
                } else {
                    logger.warn("锁续期失败: {}", lockKey);
                }
            }, 0, 5, TimeUnit.SECONDS);

            logger.info("开始处理支付: 幂等键={}, 金额={}", idempotencyKey, amount);

            // 查询幂等记录
            Optional<IdempotentRecord> existingRecord = idempotentRecordRepository.findByIdempotencyKey(idempotencyKey);
            if (existingRecord.isPresent()) {
                IdempotentRecord record = existingRecord.get();
                logger.info("幂等记录已存在: {}", record);
                record.setHitCount(record.getHitCount() + 1);
                idempotentRecordRepository.save(record);
                return record.getResponse();
            }

            // 创建新的幂等记录
            IdempotentRecord newRecord = new IdempotentRecord();
            newRecord.setIdempotencyKey(idempotencyKey);
            newRecord.setOrderNo(String.valueOf(orderId));
            newRecord.setRequest("amount=" + amount);
            newRecord.setOperation("PAYMENT");
            newRecord.setStatus(PaymentStatusEnum.PROCESSING.getCode()); // 处理中
            newRecord.setHitCount(1);
            idempotentRecordRepository.save(newRecord);

            try {
                // 模拟支付处理逻辑
                boolean paymentSuccess = performPayment(amount);

                String response;
                if (paymentSuccess) {
                    response = "Payment successful.";
                    newRecord.setStatus(PaymentStatusEnum.SUCCESS.getCode()); // 成功
                } else {
                    response = "Payment failed.";
                    newRecord.setStatus(PaymentStatusEnum.FAILED.getCode()); // 失败
                }

                // 更新幂等记录
                newRecord.setResponse(response);
                idempotentRecordRepository.save(newRecord);
                return response;

            } catch (Exception e) {
                logger.error("支付处理异常: 幂等键={}, 错误信息={}", idempotencyKey, e.getMessage(), e);
                newRecord.setResponse("Payment processing error.");
                newRecord.setStatus(2); // 失败
                idempotentRecordRepository.save(newRecord);
                return "Payment processing error.";
            }
        } finally {
            // 释放分布式锁并关闭续期任务
            scheduler.shutdown();
            redisUtil.delete(lockKey, lockValue);
        }
    }

    /**
     * 模拟支付处理逻辑
     *
     * @param amount 支付金额
     * @return 是否支付成功
     */
    private boolean performPayment(double amount) {
        logger.info("正在处理支付: 金额={}", amount);
        // 模拟支付逻辑
        return true; // 假设支付总是成功
    }
}