package com.example.demo.lottery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    private final List<byte[]> memoryHog = new ArrayList<>();

    @GetMapping("/oom")
    public String triggerOOM() {
        while (true) {
            // 每次分配 10MB
            memoryHog.add(new byte[10 * 1024 * 1024]);
        }
    }

    @GetMapping("/oom-safe")
    public String triggerOOMSafe() {
        while (true) {
            // 尝试分配 10MB
            try {
                memoryHog.add(new byte[10 * 1024 * 1024]);
            } catch (OutOfMemoryError e) {
                // 捕获内存溢出异常并返回结果
                return "Out of memory";
            }
        }
    }

    @GetMapping("/input")
    public String input() {
        return "Input received";
    }
}
