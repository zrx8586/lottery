package com.example.demo.service;

import com.example.demo.model.LotteryUser;
import com.example.demo.repository.LotteryUserRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@Service
public class UserService {

    @Resource
    private LotteryUserRepository userRepository;

    // 查询所有用户
    public List<LotteryUser> getAllUsers() {
        return userRepository.findAll();
    }

    // 根据用户名查询用户
    public Optional<LotteryUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 创建新用户
    public LotteryUser createUser(LotteryUser user) {
        return userRepository.save(user);
    }
}