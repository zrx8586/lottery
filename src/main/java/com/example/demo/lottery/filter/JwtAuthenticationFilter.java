package com.example.demo.lottery.filter;

import com.example.demo.lottery.service.TokenBlacklistService;
import com.example.demo.lottery.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 获取 Authorization 头部
        String authHeader = request.getHeader("Authorization");

        // 检查 Token 格式
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer " 前缀
            try {
                // 检查 Token 是否在黑名单中
                if (tokenBlacklistService.isBlacklisted(jwtUtil.getJti(token))) {
                    logger.warn("Token is blacklisted: {}", token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"Token is blacklisted\"}");
                    return;
                }

                // 验证并解析 Token
                String username = jwtUtil.validateToken(token);
                // 设置请求属性
                request.setAttribute("username", username);

                // 创建 UserDetails 对象
                UserDetails userDetails = createUserDetails(username);

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 设置认证信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Token 无效，返回 401
                logger.error("Token validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid token\"}");
                return;
            }
        } else {
            // 记录日志
            logger.warn("Authorization header is missing or invalid");
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }

    private UserDetails createUserDetails(String username) {
        return User.withUsername(username).password("").authorities("USER").build();
    }
}