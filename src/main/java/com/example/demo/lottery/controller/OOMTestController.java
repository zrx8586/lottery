package com.example.demo.lottery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/test")
public class OOMTestController {
    private final List<byte[]> memoryHog = new ArrayList<>();

    @GetMapping("/oom")
    public String triggerOOM() {
        while (true) {
            // 每次分配 10MB
            memoryHog.add(new byte[10 * 1024 * 1024]);
        }
    }
}
