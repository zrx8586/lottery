package com.example.demo.lottery.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    @PostConstruct
    public void init() {
        // 在服务端启动时生成新的密钥
        secretKey = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    // 生成 JWT Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setId(UUID.randomUUID().toString()) // 设置唯一标识符 JTI
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证 Token 并返回用户名
    public String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 提取 JTI
    public String getJti(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    // 获取 Token 的剩余过期时间（秒）
    public long getExpiration(String token) {
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(token)))
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