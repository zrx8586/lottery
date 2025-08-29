package com.example.demo.lottery.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 令牌桶限流示例
 * @author long_w
 */
public class TokenBucketDemo {
    public static void main(String[] args) {
        RateLimiter rateLimiter = RateLimiter.create(5); // 每秒5个请求
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            final int requestId = i;
            executor.submit(() -> {
                try {
                    Thread.sleep(200); // 每个请求间隔200ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (rateLimiter.tryAcquire()) {
                    System.out.println("Request " + requestId + " 通过 at " + System.currentTimeMillis());
                } else {
                    System.out.println("Request " + requestId + " 拒绝 at " + System.currentTimeMillis());
                }
            });
        }
        executor.shutdown();
    }
}
