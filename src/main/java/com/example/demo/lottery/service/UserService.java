package com.example.demo.lottery.service;

import com.example.demo.lottery.dao.model.User;
import com.example.demo.lottery.dao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author long_w
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // 查询所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 根据用户名查询用户
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 创建新用户
    public User createUser(User user) {
        return userRepository.save(user);
    }
}