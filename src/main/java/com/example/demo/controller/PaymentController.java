package com.example.demo.controller;

import com.example.demo.dto.request.ProcessPaymentRequest;
import com.example.demo.dto.response.BaseResponse;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<BaseResponse<String>> processPayment(@RequestBody ProcessPaymentRequest request) {
        ExecutorService executorService = null;
        try {
            // 创建一个固定大小的线程池
            executorService = Executors.newFixedThreadPool(10);

            // 模拟 10 个线程并发调用
            for (int i = 0; i < 10; i++) {
                executorService.submit(() -> {
                    String result = paymentService.processPayment(request);
                    return ResponseEntity.ok(BaseResponse.success(result));
                });
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(BaseResponse.failure("支付处理失败，请稍后重试"));
        } finally {
            // 关闭线程池
            executorService.shutdown();
        }
        return null;
    }
}