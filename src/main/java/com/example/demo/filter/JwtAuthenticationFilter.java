package com.example.demo.filter;

import com.example.demo.service.LeaderboardService;
import com.example.demo.service.TokenBlacklistService;
import com.example.demo.util.JwtUtil;
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
                if (tokenBlacklistService.isBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // 验证并解析 Token
                String username = JwtUtil.validateToken(token);
                // 设置请求属性
                request.setAttribute("username", username);

                // 创建 UserDetails 对象
                UserDetails userDetails = User.withUsername(username).password("").authorities("USER").build();

                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 设置认证信息
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Token 无效，返回 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } else {
            // Token 缺失或格式错误，返回 401
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // 记录日志
            logger.warn("Authorization header is missing or invalid");
        }

        // 继续过滤链
        filterChain.doFilter(request, response);
    }
}