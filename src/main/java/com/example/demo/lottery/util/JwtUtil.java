package com.example.demo.lottery.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "alex_demo_lottery_secret_key_alex_demo_lottery_secret_key"; // 必须至少 256 位
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    @Value("${jwt.expiration-time}")
    private long expirationTime; // 从配置文件中读取失效时间

    // 生成 JWT
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString()) // 添加唯一的 jti
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 使用配置的失效时间
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getJti(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    // 验证 JWT 并返回用户名
    public String validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token 已过期，请重新登录", e);
        } catch (Exception e) {
            throw new RuntimeException("Token 无效", e);
        }
    }

    public static long getExpiration(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();

            return (expiration.getTime() - System.currentTimeMillis()) / 1000;
        } catch (ExpiredJwtException e) {
            return 0;
        } catch (Exception e) {
            throw new RuntimeException("无法解析 Token 的过期时间", e);
        }
    }
}