package com.example.demo.lottery.ratelimiter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 漏桶算法示例
 * @author long_w
 */
public class LeakyBucketExample {
    private final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

    public void addRequest(int request) {
        if (queue.offer(request)) {
            System.out.println("Request " + request + " is added to the bucket.");
        } else {
            System.out.println("Request " + request + " is rejected.");
        }
    }

    public void processRequests() {
        new Thread(() -> {
            while (true) {
                try {
                    Integer request = queue.take();
                    System.out.println("Processing request " + request);
                    Thread.sleep(100); // Simulate processing time
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        LeakyBucketExample leakyBucket = new LeakyBucketExample();
        leakyBucket.processRequests();

        for (int i = 0; i < 20; i++) {
            leakyBucket.addRequest(i);
        }
    }
}
