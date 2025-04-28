package com.example.demo.lottery.controller;

import com.example.demo.lottery.dao.model.LotteryUser;
import com.example.demo.lottery.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    // 查询所有用户
    @GetMapping("/all")
    public ResponseEntity<List<LotteryUser>> getAllUsers() {
        List<LotteryUser> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 根据用户名查询用户
    @GetMapping("/{username}")
    public ResponseEntity<LotteryUser> getUserByUsername(@PathVariable String username) {
        Optional<LotteryUser> user = userService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 创建新用户
    @PostMapping("/create")
    public ResponseEntity<LotteryUser> createUser(@RequestBody LotteryUser user) {
        LotteryUser createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
}