package com.example.demo.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "alex_demo_lottery_secret_key_alex_demo_lottery_secret_key"; // 必须至少 256 位
    private static final long EXPIRATION_TIME = 86400000; // 1 天

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 生成 JWT
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
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
}