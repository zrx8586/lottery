package com.example.demo.lottery;

import com.example.demo.lottery.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LotteryMultiUserConcurrentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, String> userTokens;
    private Map<String, HttpHeaders> userHeaders;
    private static final int USER_COUNT = 10;
    private static final int THREADS_PER_USER = 10;
    private static final int REQUESTS_PER_THREAD = 10;

    @BeforeEach
    public void setup() {
        // 设置控制台输出编码
        System.setProperty("file.encoding", "UTF-8");
        
        // 初始化用户token和headers
        userTokens = new ConcurrentHashMap<>();
        userHeaders = new ConcurrentHashMap<>();
        
        for (int i = 1; i <= USER_COUNT; i++) {
            String username = "test_user" + i;
            String token = jwtUtil.generateToken(username);
            userTokens.put(username, token);
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json;charset=UTF-8");
            userHeaders.put(username, headers);
        }
    }

    @Test
    public void testMultiUserConcurrentDraw() throws InterruptedException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        
        // 测试参数
        Long activityId = 1L;
        int totalThreads = USER_COUNT * THREADS_PER_USER;
        int totalRequests = totalThreads * REQUESTS_PER_THREAD;

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        // 统计结果
        Map<String, AtomicInteger> userSuccessCount = new ConcurrentHashMap<>();
        Map<String, AtomicInteger> userFailureCount = new ConcurrentHashMap<>();
        Map<String, List<String>> userErrorMessages = new ConcurrentHashMap<>();
        
        // 初始化统计计数器
        for (int i = 1; i <= USER_COUNT; i++) {
            String username = "test_user" + i;
            userSuccessCount.put(username, new AtomicInteger(0));
            userFailureCount.put(username, new AtomicInteger(0));
            userErrorMessages.put(username, new ArrayList<>());
        }

        // 开始时间
        long startTime = System.currentTimeMillis();

        // 为每个用户创建多个线程
        for (int userIndex = 1; userIndex <= USER_COUNT; userIndex++) {
            final String username = "test_user" + userIndex;
            
            for (int threadIndex = 0; threadIndex < THREADS_PER_USER; threadIndex++) {
                executorService.submit(() -> {
                    for (int requestIndex = 0; requestIndex < REQUESTS_PER_THREAD; requestIndex++) {
                        try {
                            String url = String.format("http://localhost:%d/api/lottery/draw?username=%s&activityId=%d",
                                    port, username, activityId);
                            
                            HttpEntity<String> requestEntity = new HttpEntity<>(userHeaders.get(username));
                            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

                            if (response.getStatusCode().is2xxSuccessful()) {
                                userSuccessCount.get(username).incrementAndGet();
                            } else {
                                userFailureCount.get(username).incrementAndGet();
                                String responseBody = response.getBody();
                                if (responseBody != null) {
                                    try {
                                        var jsonNode = objectMapper.readTree(responseBody);
                                        String message = jsonNode.get("message").asText();
                                        userErrorMessages.get(username).add(message);
                                    } catch (Exception e) {
                                        userErrorMessages.get(username).add(new String(responseBody.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                                    }
                                }
                            }
                        } catch (Exception e) {
                            userFailureCount.get(username).incrementAndGet();
                            userErrorMessages.get(username).add(e.getMessage());
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
        }

        // 等待所有请求完成
        latch.await();
        executorService.shutdown();

        // 计算耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 打印测试结果
        out.println("\n并发测试结果：");
        out.println("总请求数：" + totalRequests);
        out.println("总耗时：" + duration + "ms");
        out.println("平均响应时间：" + (duration / totalRequests) + "ms");
        out.println("QPS：" + (totalRequests * 1000.0 / duration));
        
        // 打印每个用户的测试结果
        out.println("\n各用户测试结果：");
        for (int i = 1; i <= USER_COUNT; i++) {
            String username = "test_user" + i;
            out.println("\n用户 " + username + " 的测试结果：");
            out.println("成功请求数：" + userSuccessCount.get(username).get());
            out.println("失败请求数：" + userFailureCount.get(username).get());
            
            List<String> errors = userErrorMessages.get(username);
            if (!errors.isEmpty()) {
                out.println("错误信息：");
                errors.forEach(out::println);
            }
        }
    }
} 