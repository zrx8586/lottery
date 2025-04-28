package com.example.demo.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET_KEY = "alex_demo_lottery_secret_key_alex_demo_lottery_secret_key"; // 必须至少 256 位
    private static final long EXPIRATION_TIME = 86400000; // 1 天

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 JWT
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString()) // 添加唯一的 jti
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 小时有效期
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String getJti(String token) {
        return Jwts.parserBuilder() // Use parserBuilder
                .setSigningKey(KEY) // Updated setSigningKey
                .build() // Build the parser
                .parseClaimsJws(token)
                .getBody()
                .getId(); // Get jti
    }

    // 验证 JWT 并返回用户名
    public static String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // Token 已过期
            throw new RuntimeException("Token 已过期，请重新登录", e);
        } catch (Exception e) {
            // 其他异常
            throw new RuntimeException("Token 无效", e);
        }
    }

    public static long getExpiration(String token) {
        try {
            // 解析 Token 并获取过期时间
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(KEY) // 使用密钥验证
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration(); // 获取过期时间

            // 计算剩余过期时间（秒）
            return (expiration.getTime() - System.currentTimeMillis()) / 1000;
        } catch (ExpiredJwtException e) {
            // 如果 Token 已过期，返回 0
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("无法解析 Token 的过期时间", e);
        }
    }
}