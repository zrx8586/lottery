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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LotterySingleUserConcurrentTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private HttpHeaders headers;

    @BeforeEach
    public void setup() {
        // 生成测试用户的token
        token = jwtUtil.generateToken("test_user");

        // 设置请求头
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json;charset=UTF-8");
    }

    @Test
    public void testConcurrentDraw() throws InterruptedException {
        // 设置控制台输出编码
        System.setProperty("file.encoding", "UTF-8");
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        // 测试参数
        String username = "test_user";
        Long activityId = 1L;
        int threadCount = 100; // 并发线程数
        int requestCount = 1000; // 总请求数

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(requestCount);

        // 统计结果
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> errorMessages = new ArrayList<>();

        // 开始时间
        long startTime = System.currentTimeMillis();

        // 提交并发请求
        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    String url = String.format("http://localhost:%d/api/lottery/draw?username=%s&activityId=%d",
                            port, username, activityId);

                    // 创建带有认证头的请求实体
                    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

                    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    } else {
                        failureCount.incrementAndGet();
                        // 使用ObjectMapper解析响应
                        String responseBody = response.getBody();
                        if (responseBody != null) {
                            try {
                                // 尝试解析JSON响应
                                var jsonNode = objectMapper.readTree(responseBody);
                                String message = jsonNode.get("message").asText();
                                errorMessages.add(message);
                            } catch (Exception e) {
                                // 如果解析失败，使用原始响应
                                errorMessages.add(new String(responseBody.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                            }
                        }
                    }
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    errorMessages.add(e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有请求完成
        latch.await();
        executorService.shutdown();

        // 计算耗时
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 打印测试结果
        out.println("并发测试结果：");
        out.println("总请求数：" + requestCount);
        out.println("成功请求数：" + successCount.get());
        out.println("失败请求数：" + failureCount.get());
        out.println("总耗时：" + duration + "ms");
        out.println("平均响应时间：" + (duration / requestCount) + "ms");
        out.println("QPS：" + (requestCount * 1000.0 / duration));

        // 打印错误信息
        if (!errorMessages.isEmpty()) {
            out.println("\n错误信息：");
            errorMessages.forEach(out::println);
        }
    }
}
