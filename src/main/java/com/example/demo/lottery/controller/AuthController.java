package com.example.demo.lottery.controller;

import com.example.demo.lottery.dao.model.Role;
import com.example.demo.lottery.dao.model.User;
import com.example.demo.lottery.dao.repository.RoleRepository;
import com.example.demo.lottery.dao.repository.UserRepository;
import com.example.demo.lottery.dto.request.LoginRequest;
import com.example.demo.lottery.dto.request.RegisterRequest;
import com.example.demo.lottery.dto.response.ApiResponse;
import com.example.demo.lottery.dto.response.LoginResponse;
import com.example.demo.lottery.dto.response.UserInfoResponse;
import com.example.demo.lottery.service.TokenBlacklistService;
import com.example.demo.lottery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名不能为空"));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码不能为空"));
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名已存在"));
        }

        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(userRole);
        user.setEmail(request.getUsername() + "@example.com");
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success("注册成功", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名不能为空"));
        }

        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("密码不能为空"));
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户不存在"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("用户名或密码错误"));
        }

        String token = jwtUtil.generateToken(user.getUsername());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        loginResponse.setUsername(user.getUsername());

        return ResponseEntity.ok(ApiResponse.success("登录成功", loginResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String jti = jwtUtil.getJti(cleanedToken);
        long expiration = jwtUtil.getExpiration(cleanedToken);
        tokenBlacklistService.addToBlacklist(jti, expiration);
        return ResponseEntity.ok(ApiResponse.success("登出成功", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoResponse>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.validateToken(token.replace("Bearer ", ""));
            UserInfoResponse userInfo = new UserInfoResponse();
            userInfo.setUsername(username);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("登录已过期，请重新登录"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<UserInfoResponse>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.validateToken(token.replace("Bearer ", ""));
            UserInfoResponse userInfo = new UserInfoResponse();
            userInfo.setUsername(username);
            return ResponseEntity.ok(ApiResponse.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.error("登录已过期，请重新登录"));
        }
    }
}
