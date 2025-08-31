package com.example.demo.lottery.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class ApiTestController {


    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

}
